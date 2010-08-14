package uk.co.anthonycampbell.grails.picasa

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

import java.util.concurrent.ArrayBlockingQueue

import uk.co.anthonycampbell.grails.picasa.event.*

import com.google.gdata.data.photos.*

import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationListener

class PicasaCacheUpdateService implements InitializingBean, ApplicationListener<PicasaUpdateEvent> {
    // Must not be transactional to allow full event detection
    static transactional = false
    
    // Declare service scope
    static scope = "singleton"

    // Declare dependencies
    def grailsApplication

    /** Default background retrieval limit */
    public static final int DEFAULT_BACKGROUND_RETRIEVAL_LIMIT = 10

    // Amount of previous and subsequent photos to retrieve
    private int backgroundRetrieveLimit = DEFAULT_BACKGROUND_RETRIEVAL_LIMIT

    /**
     * Initialise config properties.
     */
    @Override
    void afterPropertiesSet() {
        //log?.info "Initialising the ${this.getClass().getSimpleName()}..."

        // Get retrieval amount from config
        backgroundRetrieveLimit = new Integer(
            grailsApplication?.config?.picasa?.backgroundRetrieveLimit ?:
                DEFAULT_BACKGROUND_RETRIEVAL_LIMIT).intValue()
        def z = backgroundRetrieveLimit
    }

    @Override
    void onApplicationEvent(final PicasaUpdateEvent event) {
        // Get the picasa service which registered the request
        final PicasaService source = event?.source
        final List<PhotoEntry> photoEntries = event?.photoEntries
        final def albumId = event?.albumId
        final def photoId = event?.photoId
        final def showAll = event?.showAll

        log?.debug "PicasaUpdateEvent received (source=$source, albumId=$albumId, photoId=$photoId, " +
            "showAll=$showAll, photoEntriesTotal=${photoEntries?.size()}, " +
            "backgroundRetrieveLimit=${backgroundRetrieveLimit})"
        
        // Do we have a service and photo entries to work with
        if (source != null && photoEntries != null &&
                photoEntries?.size() > 0 && backgroundRetrieveLimit > 0) {
            // Initialise search variables
            def found = false
            final ArrayBlockingQueue<PhotoEntry> previousPhotos =
                new ArrayBlockingQueue<PhotoEntry>(backgroundRetrieveLimit)
            final ArrayBlockingQueue<PhotoEntry> subsequentPhotos =
                new ArrayBlockingQueue<PhotoEntry>(backgroundRetrieveLimit)
            PhotoEntry lastPhoto = null

            /*
             * On the background thread lets retrieve details for photos before
             * and after the current photo in the entry list.
             */
            for (photo in photoEntries) {
                final def entryAlbumId = photo?.getAlbumId()
                final def entryPhotoId = photo?.getId()?.substring(photo?.getId()?.lastIndexOf('/') + 1,
                    photo?.getId()?.length())

                // Update photo ID to allow easier processing
                photo?.setId(entryPhotoId)

                // Add previous photo to queue
                if (lastPhoto && !found) {
                    // If full, remove oldest before attempting to add again
                    if (!previousPhotos.offer(lastPhoto)) {
                        previousPhotos.poll()
                        previousPhotos.offer(lastPhoto)
                    }
                }

                // Update previous with current
                lastPhoto = photo

                // If we've found photo, offer subsequent photos until queue is full then stop
                if (found) {
                    if (!subsequentPhotos.offer(photo)) {
                        break
                    }
                }
                
                // Have we found the photo in the entries list
                if (entryPhotoId == photoId) {
                    found = true
                }
            }

            // Begin processing subsequent photo queue
            while (!subsequentPhotos?.isEmpty()) {
                final PhotoEntry subsequentPhoto = subsequentPhotos.poll()

                log?.debug "Attempting to update PicasaService cache with subsequent photo details " +
                    "(source=$source, albumId=${subsequentPhoto?.getAlbumId()}, " +
                    "photoId=${subsequentPhoto?.getId()}, showAll=$showAll, " +
                    "backgroundRetrieveLimit=${backgroundRetrieveLimit})"

                // Use service to retrieve photo details
                source?.getPhoto(subsequentPhoto?.getAlbumId(), subsequentPhoto?.getId(), showAll, false)
            }

            // Begin processing previous photo queue
            while (!previousPhotos?.isEmpty()) {
                final PhotoEntry previousPhoto = previousPhotos.poll()

                log?.debug "Attempting to update PicasaService cache with previous photo details " +
                    "(source=$source, albumId=${previousPhoto?.getAlbumId()}, " +
                    "photoId=${previousPhoto?.getId()}, showAll=$showAll, " +
                    "backgroundRetrieveLimit=${backgroundRetrieveLimit})"

                // Use service to retrieve photo details
                source?.getPhoto(previousPhoto?.getAlbumId(), previousPhoto?.getId(), showAll, false)
            }
        } else {
            log?.error "Update to update picasa service with photo details! (source=$source, " +
                "albumId=$albumId, photoId=$photoId, showAll=$showAll, " +
                "photoEntriesTotal=${photoEntries?.size()}, " +
                "backgroundRetrieveLimit=${backgroundRetrieveLimit})"
        }
    }
}
