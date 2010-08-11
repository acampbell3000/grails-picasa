package uk.co.anthonycampbell.grails.picasa.event

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

import uk.co.anthonycampbell.grails.picasa.PicasaService

import com.google.gdata.data.photos.*

import org.slf4j.LoggerFactory

import org.springframework.context.ApplicationEvent

/**
 * An event used to indicate a Picasa service
 * update has been made.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class PicasaUpdateEvent extends ApplicationEvent {

    /** LOG */
	private static final log = LoggerFactory.getLogger(PicasaUpdateEvent.class)

    // Declare event properties
    private final String albumId
    private final String photoId
    private final boolean showAll
    private final List<PhotoEntry> photoEntries

    /**
     * Constructor.
     */
    PicasaUpdateEvent(final PicasaService source, final String albumId,
            final String photoId, final boolean showAll, final List<PhotoEntry> photoEntries) {
        super(source)        
        this.albumId = albumId
        this.photoId = photoId
        this.showAll = showAll
        this.photoEntries = photoEntries
    }
}

