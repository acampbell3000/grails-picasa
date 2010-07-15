package uk.co.anthonycampbell.grails.plugins.picasa

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

/**
 * Interface defining the available methods on the Picasa Service cache.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
interface ServiceCache {

    /**
     * Retrieve result for the provided query from the cache.
     *
     * @param the provided query.
     * @return the query result.
     */
    def get(String query)
    
    /**
     * Put provided query and result into the cache.
     *
     * @param the provided query.
     * @param the provided result.
     */
    void put(String key, def result)

    /**
     * Empty the cache.
     */
    void purge()
}

