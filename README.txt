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

A simple plug-in which provides an Ajax driven contact form with real
time validation and captcha support. Ajax forms provided by
the "remote-forms" skin available through the skin-loader plug-in.

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

	grails install-plugin contact-form

------------------------
Configuration:
------------------------
Once install your new contact form will be available at the following
URL:

    http://{HOSTNAME}:{PORT}/{APPLICATION-NAME}/contactForm

If you would prefer to change the URL to the contact form, please take
advantage of the grails-app/conf/UrlMappings.groovy file.
For example, to change the mapping from "contactForm" to "contactUs"
insert the following line into your URL mappings file:

    "/contactUs" {
        controller = "contactForm"
    }

The next step is to provide mail plug-in configuration your SMTP server
details. The configuration is available in the
grails-app/conf/Config.groovy file. The existing example configuration
is a template for use with a GMAIL account. Please update as required.

Note: This is set up to use SSL, if your SMTP server does not make use
of SSL then you should remove the "props" SSL attributes.

Finally, you need to update the captcha definition found again in
the grails-app/conf/Config.groovy file. The existing configuration
contains comments which highlight the following properties:

- Allowed characters
- Font size
- Font type
- Background size
- Background colour
- Maximum text length
- Minimum text length
- Text colour

------------------------
Further documentation:
------------------------
The contact form plug-in is dependent on both the Mail and JCaptcha
plug-ins. For additional configuration and feature information please
refer to the following documentation:

    http://www.grails.org/plugin/mail
    http://www.grails.org/plugin/jcaptcha

For further information regarding URL mapping please refer to the
following documentation:

    http://www.grails.org/URL+mapping
