Copyright 2010 Anthony Campbell

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

--------------------------------------
Grails Picasa Plug-in
--------------------------------------

A simple plug-in which provides a photo gallery driven through a Google Picasa
Web Album account. Ajax forms provided by the "remote-forms" skin available
through the skin-loader plug-in.

If you find any issues, please submit a bug on JIRA:

     http://jira.codehaus.org/browse/GRAILSPLUGINS

Please look at the CHANGES file to see what has changed since the last 
official release.

----------------------
Upgrading from an earlier release
----------------------
There shouldn't be any issues upgrading from an earlier release.

------------------------
Installation:
------------------------
To install the plug-in from the repository enter the following command:

	grails install-plugin picasa

------------------------
Configuration:
------------------------
Once installed your new picasa gallery will be available at the following
URL:

    http://{HOSTNAME}:{PORT}/{APPLICATION-NAME}/picasa

The Picasa photo gallery implements a RESTful style URL which is not
configured by default in the downloaded binary. Therefore, you must update
your grails-app/conf/UrlMappings.groovy to include the following mapping
block:

    "/photo/list/$albumId" {
        controller = "photo"
        action = "list"
    }

    "/photo/ajaxList/$albumId" {
        controller = "photo"
        action = "ajaxList"
    }

    "/photo/show/$albumId/$photoId" {
        controller = "photo"
        action = "show"
    }

    "/photo/ajaxShow/$albumId/$photoId" {
        controller = "photo"
        action = "ajaxShow"
    }

The next step is to provide the Picasa plug-in your Google Picasa web
album account details. These details should be inserted into the
grails-app/conf/Config.groovy file. The following shows an example
Picasa configuration block:

    picasa {
        // Required
        username = "joe.bloggs@gmail.com"
        password = "password"
        imgmax = 800
        thumbsize = 72
        maxResults = 500

        // Optional
        max = 10
        maxSteps = 5
        maxKeywords = 100
        maxComments = 10
        maxCommentSteps = 5
        showPrivateAlbums = false
        showPrivatePhotos = false
        useTagCache = false

        // Feeds
        rssManagingEditor = "Joe Bloggs"
    }

Configuration properties:

    * username - Your Google Picasa web album username.
    * password - Your Google Picasa web album password.
    * imgmax - The maximum width of each photo viewed through the photo controller.
    * thumbsize - The maximum width of each album's and photo's thumbnail.
    * maxResults - The maximum number of results to be returned when performing queries. This is used when making requests through the tag controller.
    * max - The maximum number of listing displayed per page.
    * maxSteps - The maximum number of steps displayed in the pagination block.
    * maxKeywords - The maximum number of keywords displayed in the tag listing result set.
    * maxComments - The maximum number of comments displayed in the show photo view.
    * maxCommentSteps - The maximum number of steps displayed in the comment pagination block.
    * showPrivateAlbums - Whether to include private albums in all album requests.
    * showPrivatePhotos - Whether to include private photos in all photo requests.
    * useTagCache - Stores locally the tags associated with each album. Reduces Google web service calls when viewing an album's photo listing.
    * rssManagingEditor = Name of author managing the RSS feed.

Note: The properties imgmax and thumbsize are subject to a valid set of values
detailed by the API's reference guide.

------------------------
Further documentation:
------------------------
The picasa plug-in is driven through the Google Picasa Web Album API.
For additional configuration and feature information please refer to
the following documentation:

    http://picasaweb.google.co.uk
    http://code.google.com/apis/picasaweb/overview.html
    http://code.google.com/apis/picasaweb/docs/2.0/reference.html#Parameters

For further information regarding URL mapping please refer to the
following documentation:

    http://www.grails.org/URL+mapping
