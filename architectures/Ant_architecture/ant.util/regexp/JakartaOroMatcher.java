/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.tools.ant.util.regexp;

import java.util.Vector;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.tools.ant.BuildException;

/**
 * Implementation of RegexpMatcher for Jakarta-ORO.
 *
 */
public class JakartaOroMatcher implements RegexpMatcher {

    private String pattern;
    // CheckStyle:VisibilityModifier OFF - bc
    protected final Perl5Compiler compiler = new Perl5Compiler();
    protected final Perl5Matcher matcher = new Perl5Matcher();
    // CheckStyle:VisibilityModifier ON

    /**
     * Constructor for JakartaOroMatcher.
     */
    public JakartaOroMatcher() {
    }

    /**
     * Set the regexp pattern from the String description.
     * @param pattern the pattern to match
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Get a String representation of the regexp pattern
     * @return the pattern
     */
    public String getPattern() {
        return this.pattern;
    }

    /**
     * Get a compiled representation of the regexp pattern
     * @param options the options
     * @return the compiled pattern
     * @throws BuildException on error
     */
    protected Pattern getCompiledPattern(int options)
        throws BuildException {
        try {
            // compute the compiler options based on the input options first
            Pattern p = compiler.compile(pattern, getCompilerOptions(options));
            return p;
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    /**
     * Does the given argument match the pattern using default options?
     * @param argument the string to match against
     * @return true if the pattern matches
     * @throws BuildException on error
     */
    public boolean matches(String argument) throws BuildException {
        return matches(argument, MATCH_DEFAULT);
    }

    /**
     * Does the given argument match the pattern?
     * @param input the string to match against
     * @param options the regex options to use
     * @return true if the pattern matches
     * @throws BuildException on error
     */
    public boolean matches(String input, int options)
        throws BuildException {
        Pattern p = getCompiledPattern(options);
        return matcher.contains(input, p);
    }

    /**
     * Returns a Vector of matched groups found in the argument
     * using default options.
     *
     * <p>Group 0 will be the full match, the rest are the
     * parenthesized subexpressions</p>.
     *
     * @param argument the string to match against
     * @return the vector of groups
     * @throws BuildException on error
     */
    public Vector getGroups(String argument) throws BuildException {
        return getGroups(argument, MATCH_DEFAULT);
    }

    /**
     * Returns a Vector of matched groups found in the argument.
     *
     * <p>Group 0 will be the full match, the rest are the
     * parenthesized subexpressions</p>.
     *
     * @param input the string to match against
     * @param options the regex options to use
     * @return the vector of groups
     * @throws BuildException on error
     */
    public Vector getGroups(String input, int options)
        throws BuildException {
        if (!matches(input, options)) {
            return null;
        }
        Vector v = new Vector();
        MatchResult mr = matcher.getMatch();
        int cnt = mr.groups();
        for (int i = 0; i < cnt; i++) {
            String match = mr.group(i);
            // treat non-matching groups as empty matches
            if (match == null) {
                match = "";
            }
            v.addElement(match);
        }
        return v;
    }

    /**
     * Convert the generic options to the regex compiler specific options.
     * @param options the generic options
     * @return the specific options
     */
    protected int getCompilerOptions(int options) {
        int cOptions = Perl5Compiler.DEFAULT_MASK;

        if (RegexpUtil.hasFlag(options, MATCH_CASE_INSENSITIVE)) {
            cOptions |= Perl5Compiler.CASE_INSENSITIVE_MASK;
        }
        if (RegexpUtil.hasFlag(options, MATCH_MULTILINE)) {
            cOptions |= Perl5Compiler.MULTILINE_MASK;
        }
        if (RegexpUtil.hasFlag(options, MATCH_SINGLELINE)) {
            cOptions |= Perl5Compiler.SINGLELINE_MASK;
        }

        return cOptions;
    }

	public static void arcanSrc() {
	org.apache.tools.ant.util.regexp.RegexpMatcher.arcanTrg();
	}

}
