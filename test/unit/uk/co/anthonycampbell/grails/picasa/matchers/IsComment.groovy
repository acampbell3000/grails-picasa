package uk.co.anthonycampbell.grails.picasa.matchers

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

import org.mockito.ArgumentMatcher

import uk.co.anthonycampbell.grails.picasa.Comment

/**
 * Matcher used by the Mockito framework to allow comments to be
 * used more easily as arguments for mocked methods.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
class IsComment extends ArgumentMatcher<Comment> {
    @Override
    public boolean matches(final Object comment) {
        return (comment instanceof Comment)
    }
}
