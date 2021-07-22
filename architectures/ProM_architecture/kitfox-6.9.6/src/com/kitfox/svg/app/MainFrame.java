/*
 * SVG Salamander
 * Copyright (c) 2004, Mark McKay
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 *   - Redistributions of source code must retain the above 
 *     copyright notice, this list of conditions and the following
 *     disclaimer.
 *   - Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * Mark McKay can be contacted at mark@kitfox.com.  Salamander and other
 * projects can be found at http://www.kitfox.com
 *
 * Created on September 6, 2004, 1:19 AM
 */

package com.kitfox.svg.app;

/**
 *
 * @author  kitfox
 */
public class MainFrame extends javax.swing.JFrame
{
    public static final long serialVersionUID = 1;
    
    /** Creates new form MainFrame */
    public MainFrame()
    {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        jPanel1 = new javax.swing.JPanel();
        bn_svgViewer = new javax.swing.JButton();
        bn_svgViewer1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        bn_quit = new javax.swing.JButton();

        setTitle("SVG Salamander - Application Launcher");
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                exitForm(evt);
            }
        });

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        bn_svgViewer.setText("SVG Viewer (No animation)");
        bn_svgViewer.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bn_svgViewerActionPerformed(evt);
            }
        });

        jPanel1.add(bn_svgViewer);

        bn_svgViewer1.setText("SVG Player (Animation)");
        bn_svgViewer1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bn_svgViewer1ActionPerformed(evt);
            }
        });

        jPanel1.add(bn_svgViewer1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        bn_quit.setText("Quit");
        bn_quit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bn_quitActionPerformed(evt);
            }
        });

        jPanel2.add(bn_quit);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }//GEN-END:initComponents

    private void bn_svgViewer1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bn_svgViewer1ActionPerformed
    {//GEN-HEADEREND:event_bn_svgViewer1ActionPerformed
        SVGPlayer.main(null);

        close();
    }//GEN-LAST:event_bn_svgViewer1ActionPerformed

    private void bn_svgViewerActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bn_svgViewerActionPerformed
    {//GEN-HEADEREND:event_bn_svgViewerActionPerformed
        SVGViewer.main(null);

        close();
    }//GEN-LAST:event_bn_svgViewerActionPerformed

    private void bn_quitActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bn_quitActionPerformed
    {//GEN-HEADEREND:event_bn_quitActionPerformed
        exitForm(null);
    }//GEN-LAST:event_bn_quitActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt)//GEN-FIRST:event_exitForm
    {
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    private void close()
    {
        this.setVisible(false);
        this.dispose();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        new MainFrame().setVisible(true);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bn_quit;
    private javax.swing.JButton bn_svgViewer;
    private javax.swing.JButton bn_svgViewer1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
    
}