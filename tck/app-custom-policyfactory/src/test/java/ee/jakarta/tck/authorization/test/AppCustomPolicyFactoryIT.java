/*
 * Copyright (c) 2024 Contributors to Eclipse Foundation.
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package ee.jakarta.tck.authorization.test;

import static ee.jakarta.tck.authorization.util.Assert.assertDefaultAccess;
import static ee.jakarta.tck.authorization.util.Assert.assertDefaultAuthenticated;
import static ee.jakarta.tck.authorization.util.Assert.assertDefaultNoAccess;
import static ee.jakarta.tck.authorization.util.Assert.assertDefaultNotAuthenticated;
import static ee.jakarta.tck.authorization.util.ShrinkWrap.mavenWar;

import ee.jakarta.tck.authorization.util.ArquillianBase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class AppCustomPolicyFactoryIT extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return mavenWar();
    }

    // Test several general conditions to make sure security
    // works in the normal way

    /**
     * Normally authenticated for a request to the default path.
     * Should have access via the role foo
     */
    @Test
    public void testAuthenticated() {
        assertDefaultAuthenticated(
            readFromServerWithCredentials("/protectedServlet", "reza", "secret1"));
    }

    /**
     * Not authenticated on the default path.
     * Should not have access, since not in the required role foo
     */
    @Test
    public void testNotAuthenticated() {
        assertDefaultNoAccess(
            readFromServer("/protectedServlet"));
    }

    /**
     * Wrongly authenticated on the default path.
     * Should not have access, since not in the required role foo
     */
    @Test
    public void testNotAuthenticatedWrongName() {
        assertDefaultNoAccess(
            readFromServer("/protectedServlet?name=romo&password=secret1"));
    }

    // Test on the special test path which a custom policy is observing

    /**
     * Should have access, despite not being in the required role foo.
     * The custom policy made an exception here.
     *
     * But, the caller should not be in any roles (specially, should not be in role foo)
     */
    @Test
    public void testNotAuthenticatedSpecial() {
        String response = readFromServer("/protectedServlet/foo/test");

        assertDefaultAccess(response);
        assertDefaultNotAuthenticated(response);
    }

}
