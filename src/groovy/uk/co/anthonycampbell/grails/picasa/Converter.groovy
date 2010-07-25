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

import com.google.gdata.data.photos.*
import com.google.gdata.data.media.mediarss.MediaKeywords

/**
 * Utility class used to convert responses from the Google GData
 * API to local domain ojbects.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
static class Converter {

    /**
     * Convert the provided AlbumFeed or AlbumEntry object into the Album domain class.
     *
     * @param item the AlbumFeed or AlbumEntry to convert.
     * @result the Album domain class.
     */
    public static Album convertToAlbumDomain(final def item) {
        // Initialise result
        final Album album = new Album()

        // Process ID
        final String id = item?.getId()
        album.albumId = id?.substring(id?.lastIndexOf('/') + 1, id?.length())

        // Attempt to persist geo location data
        final def geoPoint = new GeoPoint()
        geoPoint.latitude = item?.getGeoLocation()?.getLatitude()
        geoPoint.longitude = item?.getGeoLocation()?.getLongitude()
        album.geoLocation = (!geoPoint.hasErrors()) ? geoPoint : null

        // Check whether album has thumbail
        final def thumbnails = item?.getMediaThumbnails()
        if (thumbnails?.size() > 0) {
            album.image = thumbnails?.get(thumbnails?.size()-1)?.getUrl()
            album.width = thumbnails?.get(thumbnails?.size()-1)?.getWidth()
            album.height = thumbnails?.get(thumbnails?.size()-1)?.getHeight()
        }

        // Check whether photo has any tags
        final def keywords = item?.getMediaKeywords()?.getKeywords()
        if (keywords?.size() > 0) {
            // Add all tags
            for (final String keyword : keywords) {
                final Tag tag = new Tag()
                tag.keyword = keyword

                if (!tag.hasErrors()) {
                    album.addToTags(tag)
                }
            }
        }

        // Transfer remaining properties over to domain class
        album.name = item?.getTitle()?.getPlainText()
        album.description = item?.getDescription()?.getPlainText()
        album.location = item?.getLocation()
        album.photoCount = item?.getPhotosUsed()
        album.dateCreated = item?.getDate()
        album.isPublic = item?.getAccess()?.equals(GphotoAccess.Value.PUBLIC) ? true : false

        // Return update album
        return album
    }

    /**
     * Convert the provided PhotoFeed or PhotoEntry object into the Photo domain class.
     *
     * @param item the PhotoFeed or PhotoEntry to convert.
     * @result the Photo domain class.
     */
    public static Photo convertToPhotoDomain(final def item) {
        // Initialise result
        final Photo photo = new Photo()

        // Process ID
        final String id = item?.getId()
        photo.photoId = id?.substring(id?.lastIndexOf('/') + 1, id?.length())

        // Attempt to persist geo location data
        final def geoPoint = new GeoPoint()
        geoPoint.latitude = item?.getGeoLocation()?.getLatitude()
        geoPoint.longitude = item?.getGeoLocation()?.getLongitude()
        photo.geoLocation = (!geoPoint.hasErrors()) ? geoPoint : null

        // Check whether photo has thumbails
        final def thumbnails = item?.getMediaThumbnails()
        if (thumbnails?.size() > 0) {
            photo.thumbnailImage = thumbnails?.get(thumbnails?.size()-1)?.getUrl()
            photo.thumbnailWidth = thumbnails?.get(thumbnails?.size()-1)?.getWidth()
            photo.thumbnailHeight = thumbnails?.get(thumbnails?.size()-1)?.getHeight()
        }

        // Check whether photo has content
        final def content = item?.getMediaContents()
        if (content?.size() > 0) {
            photo.image = content?.get(content?.size()-1)?.getUrl()
            photo.width = content?.get(content?.size()-1)?.getWidth()
            photo.height = content?.get(content?.size()-1)?.getHeight()
        }

        // Check whether photo has any tags
        final def keywords = item?.getMediaKeywords()?.getKeywords()
        if (keywords?.size() > 0) {
            // Add all tags
            for (final String keyword : keywords) {
                final Tag tag = new Tag()
                tag.keyword = keyword

                if (!tag.hasErrors()) {
                    photo.addToTags(tag)
                }
            }
        }

        // Transfer remaining properties over to domain class
        photo.albumId = item?.getAlbumId()
        photo.title = item?.getTitle()?.getPlainText()
        photo.description = item?.getDescription()?.getPlainText()
        photo.cameraModel = item?.getExifTags()?.getCameraModel()
        photo.dateCreated = item?.getTimestamp()
        photo.isPublic = item?.getAlbumAccess()?.equals(GphotoAccess.Value.PUBLIC) ? true : false

        // Return updated photo
        return photo
    }

    /**
     * Convert the provided TagEntry object into the Tag domain class.
     *
     * @param entry the TagEntry to convert.
     * @result the Tag domain class.
     */
    public static Tag convertToTagDomain(final TagEntry entry) {
        // Initialise result
        final Tag tag = new Tag()

        // Process keyword
        tag.keyword = entry?.getTitle()?.getPlainText()
        tag.weight = (entry?.getWeight()) ? entry?.getWeight()?.intValue() : 0

        // Return updated tag
        return tag
    }

    /**
     * Convert the provided CommentEntry object into the Comment domain class.
     *
     * @param entry the CommentEntry to convert.
     * @result the Comment domain class.
     */
    public static Comment convertToCommentDomain(final CommentEntry entry) {
        // Initialise result
        final Comment comment = new Comment()

        // Process properties
        comment.commentId = entry?.getId()
        comment.albumId = entry?.getAlbumId()
        comment.photoId = entry?.getPhotoId()
        comment.message = entry?.getPlainTextContent()

        // Convert DateTime to java.util.Date
        final Date date = new Date()
        date.setTime(entry?.getUpdated()?.getValue())
        comment.dateCreated = date

        // Add author
        final Person person = convertToPersonDomain(entry?.getAuthors()?.get(0))
        if (!person.hasErrors()) {
            comment.author = person
        }

        // Return updated comment
        return comment
    }

    /**
     * Convert the provided com.google.gdata.data.Person object into the
     * Person domain class.
     *
     * @param entry the com.google.gdata.data.Person to convert.
     * @result the Person domain class.
     */
    public static Person convertToPersonDomain(final com.google.gdata.data.Person entry) {
        // Initalise result
        final Person person = new Person()

        // Process properties
        person.name = entry?.getName()
        person.email = entry?.getEmail()
        person.uri = entry?.getUri()

        // Return updated person
        return person
    }
}

