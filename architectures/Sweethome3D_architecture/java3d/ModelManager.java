/*
 * ModelManager.java 4 juil. 07
 *
 * Copyright (c) 2007 Emmanuel PUYBARET / eTeks <info@eteks.com>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.eteks.sweethome3d.j3d;

import java.awt.EventQueue;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryStripArray;
import javax.media.j3d.Group;
import javax.media.j3d.IndexedGeometryArray;
import javax.media.j3d.IndexedGeometryStripArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.IndexedTriangleFanArray;
import javax.media.j3d.IndexedTriangleStripArray;
import javax.media.j3d.Light;
import javax.media.j3d.Node;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.TriangleFanArray;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.eteks.sweethome3d.model.Content;
import com.eteks.sweethome3d.tools.TemporaryURLContent;
import com.eteks.sweethome3d.tools.URLContent;
import com.microcrowd.loader.java3d.max3ds.Loader3DS;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.lw3d.Lw3dLoader;

/**
 * Singleton managing 3D models cache.
 * This manager supports 3D models with an OBJ, 3DS or LWS format by default. 
 * Additional classes implementing Java 3D <code>Loader</code> interface may be 
 * specified in the <code>com.eteks.sweethome3d.j3d.additionalLoaderClasses</code>
 * (separated by a space or a colon :) to enable the support of other formats. 
 * @author Emmanuel Puybaret
 */
public class ModelManager {
  /**
   * <code>Shape3D</code> user data prefix for window pane shapes. 
   */
  public static final String WINDOW_PANE_SHAPE_PREFIX = "sweethome3d_window_pane";
  /**
   * <code>Shape3D</code> user data prefix for mirror shapes. 
   */
  public static final String MIRROR_SHAPE_PREFIX = "sweethome3d_window_mirror";

  private static final TransparencyAttributes WINDOW_PANE_TRANSPARENCY_ATTRIBUTES = 
      new TransparencyAttributes(TransparencyAttributes.NICEST, 0.5f);
  
  private static final String ADDITIONAL_LOADER_CLASSES = "com.eteks.sweethome3d.j3d.additionalLoaderClasses";
  
  private static ModelManager instance;
  
  // Map storing loaded model nodes
  private Map<Content, BranchGroup> loadedModelNodes;
  // Map storing model nodes being loaded
  private Map<Content, List<ModelObserver>> loadingModelObservers;
  // Executor used to load models
  private ExecutorService           modelsLoader;
  // List of additional loader classes
  private Class<Loader> []          additionalLoaderClasses;
  
  private ModelManager() {    
    // This class is a singleton
    this.loadedModelNodes = Collections.synchronizedMap(new WeakHashMap<Content, BranchGroup>());
    this.loadingModelObservers = new HashMap<Content, List<ModelObserver>>();
    List<Class<Loader>> loaderClasses = new ArrayList<Class<Loader>>();
    String loaderClassNames = System.getProperty(ADDITIONAL_LOADER_CLASSES);
    if (loaderClassNames != null) {
      for (String loaderClassName : loaderClassNames.split("\\s|:")) {
        try {
          loaderClasses.add(getLoaderClass(loaderClassName));
        } catch (IllegalArgumentException ex) {
          System.err.println("Invalid loader class " + loaderClassName + ":\n" + ex.getMessage());
        }
      }
    }
    this.additionalLoaderClasses = loaderClasses.toArray(new Class [loaderClasses.size()]);
  }

  /**
   * Returns the class of name <code>loaderClassName</code>.
   */
  @SuppressWarnings("unchecked")
  private Class<Loader> getLoaderClass(String loaderClassName) {
    try {
      Class<Loader> loaderClass = (Class<Loader>)getClass().getClassLoader().loadClass(loaderClassName);
      if (!Loader.class.isAssignableFrom(loaderClass)) {
        throw new IllegalArgumentException(loaderClassName + " not a subclass of " + Loader.class.getName());
      } else if (Modifier.isAbstract(loaderClass.getModifiers()) || !Modifier.isPublic(loaderClass.getModifiers())) {
        throw new IllegalArgumentException(loaderClassName + " not a public static class");
      }
      Constructor<Loader> constructor = loaderClass.getConstructor(new Class [0]);
      // Try to instantiate it now to see if it won't cause any problem
      constructor.newInstance(new Object [0]);
      return loaderClass;
    } catch (ClassNotFoundException ex) {
      throw new IllegalArgumentException(ex.getMessage(), ex);
    } catch (NoSuchMethodException ex) {
      throw new IllegalArgumentException(ex.getMessage(), ex);
    } catch (InvocationTargetException ex) {
      throw new IllegalArgumentException(ex.getMessage(), ex);
    } catch (IllegalAccessException ex) {
      throw new IllegalArgumentException(loaderClassName + " constructor not accessible");
    } catch (InstantiationException ex) {
      throw new IllegalArgumentException(loaderClassName + " not a public static class");
    }
  }
  
  /**
   * Returns an instance of this singleton. 
   */
  public static ModelManager getInstance() {
    if (instance == null) {
      instance = new ModelManager();
    }
    return instance;
  }

  /**
   * Shutdowns the multithreaded service that load textures. 
   */
  public void clear() {
    if (this.modelsLoader != null) {
      this.modelsLoader.shutdownNow();
      this.modelsLoader = null;
    }
    this.loadedModelNodes.clear();
  }
  
  /**
   * Returns the size of 3D shapes under <code>node</code>.
   * This method computes the exact box that contains all the shapes,
   * contrary to <code>node.getBounds()</code> that returns a bounding 
   * sphere for a scene.
   */
  public Vector3f getSize(Node node) {
    BoundingBox bounds = getBounds(node);
    Point3d lower = new Point3d();
    bounds.getLower(lower);
    Point3d upper = new Point3d();
    bounds.getUpper(upper);
    return new Vector3f((float)(upper.x - lower.x), (float)(upper.y - lower.y), (float)(upper.z - lower.z));
  }
  
  /**
   * Returns the bounds of 3D shapes under <code>node</code>.
   * This method computes the exact box that contains all the shapes,
   * contrary to <code>node.getBounds()</code> that returns a bounding 
   * sphere for a scene.
   */
  public BoundingBox getBounds(Node node) {
    BoundingBox objectBounds = new BoundingBox(
        new Point3d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
        new Point3d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
    computeBounds(node, objectBounds);
    Point3d lower = new Point3d();
    objectBounds.getLower(lower);
    if (lower.x == Double.POSITIVE_INFINITY) {
      throw new IllegalArgumentException("Node has no bounds");
    }
    return objectBounds;
  }
  
  private void computeBounds(Node node, BoundingBox bounds) {
    if (node instanceof Group) {
      // Compute the bounds of all the node children
      Enumeration<?> enumeration = ((Group)node).getAllChildren();
      while (enumeration.hasMoreElements ()) {
        computeBounds((Node)enumeration.nextElement (), bounds);
      }
    } else if (node instanceof Shape3D) {
      Bounds shapeBounds = ((Shape3D)node).getBounds();
      bounds.combine(shapeBounds);
    }
  }

  /**
   * Returns a transform group that will transform the model <code>node</code>
   * to let it fill a box of the given <code>width</code> centered on the origin.
   * @param node     the root of a model with any size and location
   * @param modelRotation the rotation applied to the model at the end 
   *                 or <code>null</code> if no transformation should be applied to node.
   * @param width    the width of the box
   */
  public TransformGroup getNormalizedTransformGroup(Node node, float [][] modelRotation, float width) {
    // Get model bounding box size
    BoundingBox modelBounds = ModelManager.getInstance().getBounds(node);
    Point3d lower = new Point3d();
    modelBounds.getLower(lower);
    Point3d upper = new Point3d();
    modelBounds.getUpper(upper);
    
    // Translate model to its center
    Transform3D translation = new Transform3D();
    translation.setTranslation(
        new Vector3d(-lower.x - (upper.x - lower.x) / 2, 
            -lower.y - (upper.y - lower.y) / 2, 
            -lower.z - (upper.z - lower.z) / 2));      
    // Scale model to make it fill a 1 unit wide box
    Transform3D scaleOneTransform = new Transform3D();
    scaleOneTransform.setScale (
        new Vector3d(width / (upper.x -lower.x), 
            width / (upper.y - lower.y), 
            width / (upper.z - lower.z)));
    scaleOneTransform.mul(translation);
    Transform3D modelTransform = new Transform3D();
    if (modelRotation != null) {
      // Apply model rotation
      Matrix3f modelRotationMatrix = new Matrix3f(modelRotation [0][0], modelRotation [0][1], modelRotation [0][2],
          modelRotation [1][0], modelRotation [1][1], modelRotation [1][2],
          modelRotation [2][0], modelRotation [2][1], modelRotation [2][2]);
      modelTransform.setRotation(modelRotationMatrix);
    }
    modelTransform.mul(scaleOneTransform);
    
    return new TransformGroup(modelTransform);
  }
  
  /**
   * Reads asynchronously a 3D node from <code>content</code> with supported loaders
   * and notifies the loaded model to the given <code>modelObserver</code> once available. 
   * @param content an object containing a model
   * @param modelObserver the observer that will be notified once the model is available
   *    or if an error happens
   */
  public void loadModel(Content content,
                        ModelObserver modelObserver) {
    loadModel(content, false, modelObserver);
  }
  
  /**
   * Reads a 3D node from <code>content</code> with supported loaders
   * and notifies the loaded model to the given <code>modelObserver</code> once available.
   * @param content an object containing a model
   * @param synchronous if <code>true</code>, this method will return only once model content is loaded
   * @param modelObserver the observer that will be notified once the model is available
   *    or if an error happens. When the model is loaded asynchronously, this method should be called
   *    in Event Dispatch Thread and observer will be notified in EDT, otherwise it will be notified 
   *    in the same thread as the caller.
   */
  public void loadModel(final Content content,
                        boolean synchronous,
                        ModelObserver modelObserver) {
    BranchGroup modelRoot = this.loadedModelNodes.get(content);
    if (modelRoot == null) {
      if (synchronous) {
        try {
          modelRoot = loadModel(content);
        } catch (IOException ex) {
          modelObserver.modelError(ex);
        }
        // Store in cache a model node for future copies 
        this.loadedModelNodes.put(content, (BranchGroup)modelRoot);
      } else {
        if (this.modelsLoader == null) {
          this.modelsLoader = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }
        List<ModelObserver> observers = this.loadingModelObservers.get(content);
        if (observers != null) {
          // If observers list exists, content model is already being loaded
          // register observer for future notification
          observers.add(modelObserver);
        } else {
          // Create a list of observers that will be notified once content model is loaded
          observers = new ArrayList<ModelObserver>();
          observers.add(modelObserver);
          this.loadingModelObservers.put(content, observers);
          
          // Load the model in an other thread
          this.modelsLoader.execute(new Runnable() {
            public void run() {
              try {
                final BranchGroup loadedModel = loadModel(content);
                // Update loaded models cache and notify registered observers
                loadedModelNodes.put(content, loadedModel);
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                      for (final ModelObserver observer : loadingModelObservers.remove(content)) {
                        BranchGroup modelNode = (BranchGroup)loadedModel.cloneTree(true);
                        observer.modelUpdated(modelNode);
                      }
                    }
                  });
              } catch (final IOException ex) {
                synchronized (loadedModelNodes) {
                  EventQueue.invokeLater(new Runnable() {
                      public void run() {
                        for (final ModelObserver observer : loadingModelObservers.remove(content)) {
                          observer.modelError(ex);
                        }
                      }
                    });
                }                  
              }
            }
          });
        }
        return;
      }
    } 
    
    // Notify cached model to observer with a clone of the model
    modelObserver.modelUpdated((BranchGroup)modelRoot.cloneTree(true));
  }
    
  /**
   * Returns the node loaded synchronously from <code>content</code> with supported loaders. 
   * This method is threadsafe and may be called from any thread.
   * @param content an object containing a model
   */
  public BranchGroup loadModel(Content content) throws IOException {
    // Ensure we use a URLContent object
    URLContent urlContent;
    if (content instanceof URLContent) {
      urlContent = (URLContent)content;
    } else {
      urlContent = TemporaryURLContent.copyToTemporaryURLContent(content);
    }
    
    Loader3DS loader3DSWithNoStackTraces = new Loader3DS() {
      @Override
      public Scene load(URL url) throws FileNotFoundException {
        PrintStream defaultSystemErrorStream = System.err;
        try {
          // Ignore stack traces on System.err during 3DS file loading
          System.setErr(new PrintStream (new OutputStream() {
              @Override
              public void write(int b) throws IOException {
                // Do nothing
              }
            }));
          // Default load
          return super.load(url);
        } finally {
          // Reset default err print stream
          System.setErr(defaultSystemErrorStream);
        }
      }
    };

    Loader []  defaultLoaders = new Loader [] {new OBJLoader(),
                                               loader3DSWithNoStackTraces,
                                               new Lw3dLoader()};
    Loader [] loaders = new Loader [defaultLoaders.length + this.additionalLoaderClasses.length];
    System.arraycopy(defaultLoaders, 0, loaders, 0, defaultLoaders.length);
    for (int i = 0; i < this.additionalLoaderClasses.length; i++) {
      try {
        loaders [defaultLoaders.length + i] = this.additionalLoaderClasses [i].newInstance();
      } catch (InstantiationException ex) {
        // Can't happen: getLoaderClass checked this class is instantiable
        throw new InternalError(ex.getMessage());
      } catch (IllegalAccessException ex) {
        // Can't happen: getLoaderClass checked this class is instantiable 
        throw new InternalError(ex.getMessage());
      } 
    }
    
    Exception lastException = null;
    for (Loader loader : loaders) {
      try {     
        // Ask loader to ignore lights, fogs...
        loader.setFlags(loader.getFlags() 
            & ~(Loader.LOAD_LIGHT_NODES | Loader.LOAD_FOG_NODES 
                | Loader.LOAD_BACKGROUND_NODES | Loader.LOAD_VIEW_GROUPS));
        // Return the first scene that can be loaded from model URL content
        Scene scene = loader.load(urlContent.getURL());

        BranchGroup modelNode = scene.getSceneGroup();
        // If model doesn't have any child, consider the file as wrong
        if (modelNode.numChildren() == 0) {
          throw new IllegalArgumentException("Empty model");
        }
        
        // Update transparency of scene window panes shapes
        updateShapeNamesAndWindowPanesTransparency(scene);
        
        // Turn off lights because some loaders don't take into account the ~LOAD_LIGHT_NODES flag
        turnOffLights(modelNode);

        return modelNode;
      } catch (IllegalArgumentException ex) {
        lastException = ex;
      } catch (IncorrectFormatException ex) {
        lastException = ex;
      } catch (ParsingErrorException ex) {
        lastException = ex;
      } catch (IOException ex) {
        lastException = ex;
      } catch (RuntimeException ex) {
        // Take into account exceptions of Java 3D 1.5 ImageException class
        // in such a way program can run in Java 3D 1.3.1
        if (ex.getClass().getName().equals("com.sun.j3d.utils.image.ImageException")) {
          lastException = ex;
        } else {
          throw ex;
        }
      }
    }
    
    if (lastException instanceof IncorrectFormatException) {
      IOException incorrectFormatException = new IOException("Incorrect format");
      incorrectFormatException.initCause(lastException);
      throw incorrectFormatException;
    } else if (lastException instanceof ParsingErrorException) {
      IOException incorrectFormatException = new IOException("Parsing error");
      incorrectFormatException.initCause(lastException);
      throw incorrectFormatException;
    } else {
      throw (IOException)lastException;
    } 
  }  
  
  /**
   * Updates the name of scene shapes and transparency window panes shapes.
   */
  private void updateShapeNamesAndWindowPanesTransparency(Scene scene) {
    Map<String, Object> namedObjects = scene.getNamedObjects();
    for (Map.Entry<String, Object> entry : namedObjects.entrySet()) {
      if (entry.getValue() instanceof Shape3D) {
        // Assign shape name to its user data
        Shape3D shape = (Shape3D)entry.getValue();
        shape.setUserData(entry.getKey());
        if (entry.getKey().startsWith(WINDOW_PANE_SHAPE_PREFIX)) {
          Appearance appearance = shape.getAppearance();
          if (appearance == null) {
            appearance = new Appearance();
            shape.setAppearance(appearance);
          }
          if (appearance.getTransparencyAttributes() == null) {
            appearance.setTransparencyAttributes(WINDOW_PANE_TRANSPARENCY_ATTRIBUTES);
          }
        }
      }
    }
  }
  
  /**
   * Turn off light nodes of <code>node</code> children.
   */
  private void turnOffLights(Node node) {
    if (node instanceof Group) {
      // Remove lights of all children
      Enumeration<?> enumeration = ((Group)node).getAllChildren(); 
      while (enumeration.hasMoreElements()) {
        turnOffLights((Node)enumeration.nextElement());
      }
    } else if (node instanceof Light) {
      ((Light)node).setEnable(false);
    }
  }

  /**
   * Returns the 2D area of the 3D shapes children of the given <code>node</code> 
   * projected on the floor (plan y = 0). 
   */
  public Area getAreaOnFloor(Node node) {
    Area modelAreaOnFloor = new Area();
    computeAreaOnFloor(node, node, modelAreaOnFloor);
    return modelAreaOnFloor;
  }
  
  /**
   * Computes the 2D area on floor of a the 3D shapes children of <code>node</code>.
   */
  private void computeAreaOnFloor(Node parent, Node node, Area nodeArea) {
    if (node instanceof Group) {
      // Compute all children
      Enumeration<?> enumeration = ((Group)node).getAllChildren(); 
      while (enumeration.hasMoreElements()) {
        computeAreaOnFloor(parent, (Node)enumeration.nextElement(), nodeArea);
      }
    } else if (node instanceof Shape3D) {
      Shape3D shape = (Shape3D)node;
      // Retrieve transformation applied to vertices
      Transform3D transformationToParent = getTransformationToParent(parent, node);
      // Compute shape geometries area
      for (int i = 0, n = shape.numGeometries(); i < n; i++) {
        computeGeometryAreaOnFloor(shape.getGeometry(i), transformationToParent, nodeArea);
      }
    }    
  }
  
  /**
   * Returns the transformation applied to a <code>child</code> 
   * on the path to <code>parent</code>. 
   */
  private Transform3D getTransformationToParent(Node parent, Node child) {
    Transform3D transform = new Transform3D();
    if (child instanceof TransformGroup) {
      ((TransformGroup)child).getTransform(transform);
    }
    if (child != parent) {
      Transform3D parentTransform = getTransformationToParent(parent, child.getParent());
      parentTransform.mul(transform);
      return parentTransform;
    } else {
      return transform;
    }
  }
  
  /**
   * Computes the area on floor of a 3D geometry.
   */
  private void computeGeometryAreaOnFloor(Geometry geometry, 
                                          Transform3D transformationToParent, 
                                          Area nodeArea) {
    if (geometry instanceof GeometryArray) {
      GeometryArray geometryArray = (GeometryArray)geometry;      

      int vertexCount = geometryArray.getVertexCount();
      float [] vertices = new float [vertexCount * 2]; 
      Point3f vertex = new Point3f();
      if ((geometryArray.getVertexFormat() & GeometryArray.BY_REFERENCE) != 0) {
        if ((geometryArray.getVertexFormat() & GeometryArray.INTERLEAVED) != 0) {
          float [] vertexData = geometryArray.getInterleavedVertices();
          int vertexSize = vertexData.length / vertexCount;
          // Store vertices coordinates 
          for (int index = 0, i = vertexSize - 3; index < vertices.length; i += vertexSize) {
            vertex.x = vertexData [i];
            vertex.y = vertexData [i + 1];
            vertex.z = vertexData [i + 2];
            transformationToParent.transform(vertex);
            vertices [index++] = vertex.x;
            vertices [index++] = vertex.z;
          }
        } else {
          // Store vertices coordinates
          float [] vertexCoordinates = geometryArray.getCoordRefFloat();
          for (int index = 0, i = 0; index < vertices.length; i += 3) {
            vertex.x = vertexCoordinates [i];
            vertex.y = vertexCoordinates [i + 1];
            vertex.z = vertexCoordinates [i + 2];
            transformationToParent.transform(vertex);
            vertices [index++] = vertex.x;
            vertices [index++] = vertex.z;
          }
        }
      } else {
        // Store vertices coordinates
        for (int index = 0, i = 0; index < vertices.length; i++) {
          geometryArray.getCoordinate(i, vertex);
          transformationToParent.transform(vertex);
          vertices [index++] = vertex.x;
          vertices [index++] = vertex.z;
        }
      }

      // Create path from triangles or quadrilaterals of geometry
      GeneralPath geometryPath = null;
      if (geometryArray instanceof IndexedGeometryArray) {
        if (geometryArray instanceof IndexedTriangleArray) {
          IndexedTriangleArray triangleArray = (IndexedTriangleArray)geometryArray;
          geometryPath = new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000);
          for (int i = 0, triangleIndex = 0, n = triangleArray.getIndexCount(); i < n; i += 3) {
            addIndexedTriangleToPath(triangleArray, i, i + 1, i + 2, vertices, 
                geometryPath, triangleIndex++, nodeArea);
          }
        } else if (geometryArray instanceof IndexedQuadArray) {
          IndexedQuadArray quadArray = (IndexedQuadArray)geometryArray;
          geometryPath = new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000);
          for (int i = 0, quadrilateralIndex = 0, n = quadArray.getIndexCount(); i < n; i += 4) {
            addIndexedQuadrilateralToPath(quadArray, i, i + 1, i + 2, i + 3, vertices, 
                geometryPath, quadrilateralIndex++, nodeArea); 
          }
        } else if (geometryArray instanceof IndexedGeometryStripArray) {
          IndexedGeometryStripArray geometryStripArray = (IndexedGeometryStripArray)geometryArray;
          int [] stripIndexCounts = new int [geometryStripArray.getNumStrips()];
          geometryStripArray.getStripIndexCounts(stripIndexCounts);
          geometryPath = new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000);
          int initialIndex = 0; 
          
          if (geometryStripArray instanceof IndexedTriangleStripArray) {
            for (int strip = 0, triangleIndex = 0; strip < stripIndexCounts.length; strip++) {
              for (int i = initialIndex, n = initialIndex + stripIndexCounts [strip] - 2, j = 0; i < n; i++, j++) {
                if (j % 2 == 0) {
                  addIndexedTriangleToPath(geometryStripArray, i, i + 1, i + 2, vertices, 
                      geometryPath, triangleIndex++, nodeArea); 
                } else { // Vertices of odd triangles are in reverse order               
                  addIndexedTriangleToPath(geometryStripArray, i, i + 2, i + 1, vertices, 
                      geometryPath, triangleIndex++, nodeArea);
                }
              }
              initialIndex += stripIndexCounts [strip];
            }
          } else if (geometryStripArray instanceof IndexedTriangleFanArray) {
            for (int strip = 0, triangleIndex = 0; strip < stripIndexCounts.length; strip++) {
              for (int i = initialIndex, n = initialIndex + stripIndexCounts [strip] - 2; i < n; i++) {
                addIndexedTriangleToPath(geometryStripArray, initialIndex, i + 1, i + 2, vertices, 
                    geometryPath, triangleIndex++, nodeArea); 
              }
              initialIndex += stripIndexCounts [strip];
            }
          }
        }
      } else {
        if (geometryArray instanceof TriangleArray) {
          TriangleArray triangleArray = (TriangleArray)geometryArray;
          geometryPath = new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000);
          for (int i = 0, triangleIndex = 0; i < vertexCount; i += 3) {
            addTriangleToPath(triangleArray, i, i + 1, i + 2, vertices, 
                geometryPath, triangleIndex++, nodeArea);
          }
        } else if (geometryArray instanceof QuadArray) {
          QuadArray quadArray = (QuadArray)geometryArray;
          geometryPath = new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000);
          for (int i = 0, quadrilateralIndex = 0; i < vertexCount; i += 4) {
            addQuadrilateralToPath(quadArray, i, i + 1, i + 2, i + 3, vertices, 
                geometryPath, quadrilateralIndex++, nodeArea);
          }
        } else if (geometryArray instanceof GeometryStripArray) {
          GeometryStripArray geometryStripArray = (GeometryStripArray)geometryArray;
          int [] stripVertexCounts = new int [geometryStripArray.getNumStrips()];
          geometryStripArray.getStripVertexCounts(stripVertexCounts);
          geometryPath = new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000);
          int initialIndex = 0;
          
          if (geometryStripArray instanceof TriangleStripArray) {
            for (int strip = 0, triangleIndex = 0; strip < stripVertexCounts.length; strip++) {
              for (int i = initialIndex, n = initialIndex + stripVertexCounts [strip] - 2, j = 0; i < n; i++, j++) {
                if (j % 2 == 0) {
                  addTriangleToPath(geometryStripArray, i, i + 1, i + 2, vertices, 
                      geometryPath, triangleIndex++, nodeArea);
                } else { // Vertices of odd triangles are in reverse order               
                  addTriangleToPath(geometryStripArray, i, i + 2, i + 1, vertices, 
                      geometryPath, triangleIndex++, nodeArea);
                }
              }
              initialIndex += stripVertexCounts [strip];
            }
          } else if (geometryStripArray instanceof TriangleFanArray) {
            for (int strip = 0, triangleIndex = 0; strip < stripVertexCounts.length; strip++) {
              for (int i = initialIndex, n = initialIndex + stripVertexCounts [strip] - 2; i < n; i++) {
                addTriangleToPath(geometryStripArray, initialIndex, i + 1, i + 2, vertices, 
                    geometryPath, triangleIndex++, nodeArea);
              }
              initialIndex += stripVertexCounts [strip];
            }
          }
        }
      }
      
      if (geometryPath != null) {
        nodeArea.add(new Area(geometryPath));
      }
    } 
  }

  /**
   * Adds to <code>nodePath</code> the triangle joining vertices at 
   * vertexIndex1, vertexIndex2, vertexIndex3 indices.
   */
  private void addIndexedTriangleToPath(IndexedGeometryArray geometryArray, 
                                    int vertexIndex1, int vertexIndex2, int vertexIndex3, 
                                    float [] vertices, 
                                    GeneralPath geometryPath, int triangleIndex, Area nodeArea) {
    addTriangleToPath(geometryArray, geometryArray.getCoordinateIndex(vertexIndex1), 
        geometryArray.getCoordinateIndex(vertexIndex2), 
        geometryArray.getCoordinateIndex(vertexIndex3), vertices, geometryPath, triangleIndex, nodeArea);
  }
  
  /**
   * Adds to <code>nodePath</code> the quadrilateral joining vertices at 
   * vertexIndex1, vertexIndex2, vertexIndex3, vertexIndex4 indices.
   */
  private void addIndexedQuadrilateralToPath(IndexedGeometryArray geometryArray, 
                                         int vertexIndex1, int vertexIndex2, int vertexIndex3, int vertexIndex4, 
                                         float [] vertices, 
                                         GeneralPath geometryPath, int quadrilateralIndex, Area nodeArea) {
    addQuadrilateralToPath(geometryArray, geometryArray.getCoordinateIndex(vertexIndex1), 
        geometryArray.getCoordinateIndex(vertexIndex2), 
        geometryArray.getCoordinateIndex(vertexIndex3), 
        geometryArray.getCoordinateIndex(vertexIndex4), vertices, geometryPath, quadrilateralIndex, nodeArea);
  }
  
  /**
   * Adds to <code>nodePath</code> the triangle joining vertices at 
   * vertexIndex1, vertexIndex2, vertexIndex3 indices, 
   * only if the triangle has a positive orientation. .
   */
  private void addTriangleToPath(GeometryArray geometryArray, 
                             int vertexIndex1, int vertexIndex2, int vertexIndex3, 
                             float [] vertices, 
                             GeneralPath geometryPath, int triangleIndex, Area nodeArea) {
    float xVertex1 = vertices [2 * vertexIndex1];
    float yVertex1 = vertices [2 * vertexIndex1 + 1];
    float xVertex2 = vertices [2 * vertexIndex2];
    float yVertex2 = vertices [2 * vertexIndex2 + 1];
    float xVertex3 = vertices [2 * vertexIndex3];
    float yVertex3 = vertices [2 * vertexIndex3 + 1];
    if ((xVertex2 - xVertex1) * (yVertex3 - yVertex2) - (yVertex2 - yVertex1) * (xVertex3 - xVertex2) > 0) {
      if (triangleIndex > 0 && triangleIndex % 1000 == 0) {
        // Add now current path to area otherwise area gets too slow
        nodeArea.add(new Area(geometryPath));
        geometryPath.reset();
      }
      geometryPath.moveTo(xVertex1, yVertex1);      
      geometryPath.lineTo(xVertex2, yVertex2);      
      geometryPath.lineTo(xVertex3, yVertex3);
      geometryPath.closePath();
    }
  }
  
  /**
   * Adds to <code>nodePath</code> the quadrilateral joining vertices at 
   * vertexIndex1, vertexIndex2, vertexIndex3, vertexIndex4 indices, 
   * only if the quadrilateral has a positive orientation. 
   */
  private void addQuadrilateralToPath(GeometryArray geometryArray, 
                                      int vertexIndex1, int vertexIndex2, int vertexIndex3, int vertexIndex4, 
                                      float [] vertices, 
                                      GeneralPath geometryPath, int quadrilateralIndex, Area nodeArea) {
    float xVertex1 = vertices [2 * vertexIndex1];
    float yVertex1 = vertices [2 * vertexIndex1 + 1];
    float xVertex2 = vertices [2 * vertexIndex2];
    float yVertex2 = vertices [2 * vertexIndex2 + 1];
    float xVertex3 = vertices [2 * vertexIndex3];
    float yVertex3 = vertices [2 * vertexIndex3 + 1];
    if ((xVertex2 - xVertex1) * (yVertex3 - yVertex2) - (yVertex2 - yVertex1) * (xVertex3 - xVertex2) > 0) {
      if (quadrilateralIndex > 0 && quadrilateralIndex % 1000 == 0) {
        // Add now current path to area otherwise area gets too slow
        nodeArea.add(new Area(geometryPath));
        geometryPath.reset();
      }
      geometryPath.moveTo(xVertex1, yVertex1);      
      geometryPath.lineTo(xVertex2, yVertex2);      
      geometryPath.lineTo(xVertex3, yVertex3);
      geometryPath.lineTo(vertices [2 * vertexIndex4], vertices [2 * vertexIndex4 + 1]);
      geometryPath.closePath();
    }
  }

  /**
   * An observer that receives model loading notifications. 
   */
  public static interface ModelObserver {
    public void modelUpdated(BranchGroup modelRoot); 
    
    public void modelError(Exception ex);
  }
}
