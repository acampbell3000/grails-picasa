package uk.co.anthonycampbell.grails.plugins.picasa.cache

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

import groovy.transform.Synchronized

/**
 * Default implementation of the PicasaService cache.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PicasaServiceCache implements ServiceCache {

    // Declare cache data structure
    private static final Map<Long, Map> PICASA_SERVICE_CACHE = new LinkedHashMap<Long, Map>()
    private static final long DEFAULT_CACHE_TIMEOUT = 60000

    // Singleton
    private static PicasaServiceCache picasaServiceCache

    // Instance properties
    private def grailsApplication
    private def cacheTimeout

    private PicasaServiceCache(final def cacheTimeout) {
        this.cacheTimeout = cacheTimeout
        if (!this.cacheTimeout) {
            this.cacheTimeout = DEFAULT_CACHE_TIMEOUT
        }
    }

    /**
     * Only return a single instance of the PicasaServiceCache
     */
    static PicasaServiceCache getInstance(final def cacheTimeout) {
        return (picasaServiceCache) ?: new PicasaServiceCache(cacheTimeout)
    }

    @Override
    @Synchronized
    def get(final String query) {
        // Initialise result
        def result = null

        if (PICASA_SERVICE_CACHE?.size() > 0) {
            // First check timestamp
            final Calendar today = Calendar.getInstance()
            final long todayInMillis = today?.getTimeInMillis()
            boolean clear = false

            // Remove any timed out cache entries
            final def keys = PICASA_SERVICE_CACHE?.keySet()
            def mostRecentEntry
            for (final def time in keys) {
                mostRecentEntry = time
                // If timed out clear whole cache
                if ((todayInMillis - this.cacheTimeout) > mostRecentEntry) {
                    purge()
                    clear = true
                    break
                }
            }

            // Post clear do we have any cache left
            if (!clear && PICASA_SERVICE_CACHE?.containsKey(mostRecentEntry)) {
                final Map entry = PICASA_SERVICE_CACHE.get(mostRecentEntry)

                // Retrieve result for selected query
                if (entry?.containsKey(query)) {
                    result = entry?.get(query)
                }
            }
        }

        // Return result
        return result
    }

    @Override
    @Synchronized
    void put(final String key, final def result) {
        // Create record
        final Map record = new HashMap()
        record.put(key, result)

        // Cache timestamp entry
        def timestamp

        // Get most recent entry
        final def keys = PICASA_SERVICE_CACHE?.keySet()
        for (final def time in keys) {
            timestamp = time
            break
        }

        // Validate
        if (!timestamp) {
            // Get today's timestamp
            final Calendar today = Calendar.getInstance()
            timestamp = today?.getTimeInMillis()
        }

        // Insert into cache
        PICASA_SERVICE_CACHE?.put(timestamp, record)
    }

    @Override
    void purge() {
        PICASA_SERVICE_CACHE.clear()
    }
}

