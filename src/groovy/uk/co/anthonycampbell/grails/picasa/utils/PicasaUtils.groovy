package uk.co.anthonycampbell.grails.picasa.utils

/**
 * Copyright 2010 Anthony Campbell (anthonycampbell.co.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import grails.util.Environment
import grails.util.Metadata

/**
 * Utility class for the Picasa Grails plug-in.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PicasaUtils {
    
	/**
     * Helper method to check whether environment is class reload enable
     * or if the plug-in is running tests.
     *
     * @return whether the environment is class reloadable.
     */
	static isEnvironmentClassReloadable() {
		final def env = Environment.current
		env.reloadEnabled ||
            (Metadata.current.getApplicationName() == "scoped-proxy" && env == Environment.TEST)
	}
}
