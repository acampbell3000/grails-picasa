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
Grails Contact Form
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
Once install your new picasa gallery will be available at the following
URL:

    http://{HOSTNAME}:{PORT}/{APPLICATION-NAME}/picasa

If you would prefer to change the URL to the contact form, please take
advantage of the grails-app/conf/UrlMappings.groovy file.
For example, to change the mapping from "picasa" to "gallery"
insert the following line into your URL mappings file:

    "/gallery" {
        controller = "picasa"
    }

The next step is to provide the neccessary configuration and your Google
Picasa Web Album account details. The configuration is available in the
grails-app/conf/Config.groovy file. The existing example represents a
standard configuration. Please update as required.

------------------------
Further documentation:
------------------------
The picasa plug-in is driven through the Google Picasa Web Album API.
For additional configuration and feature information please refer to
the following documentation:

    http://picasaweb.google.co.uk
    http://code.google.com/apis/picasaweb/overview.html

For further information regarding URL mapping please refer to the
following documentation:

    http://www.grails.org/URL+mapping
