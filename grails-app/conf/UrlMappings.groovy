
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

class UrlMappings {
        static mappings = {
            "/$controller/$action?/$id?"{
            constraints {
                // apply constraints here
            }
        }

        "/album/list/feed/$feed" {
            controller = "album"
            action = "list"
        }

        "/photo/list/$albumId" {
            controller = "photo"
            action = "list"
        }

        "/photo/list/$albumId/feed/$feed" {
            controller = "photo"
            action = "list"
        }

        "/photo/ajaxList/$albumId" {
            controller = "photo"
            action = "ajaxList"
        }

        "/photo/ajaxList/$albumId/feed/$feed" {
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

        "/tag/show/$id/feed/$feed" {
            controller = "tag"
            action = "show"
        }

        "/tag/list/feed/$feed" {
            controller = "tag"
            action = "list"
        }

        "/comment/list/$albumId/$photoId" {
            controller = "comment"
            action = "list"
        }

        "/comment/ajaxList/$albumId/$photoId" {
            controller = "comment"
            action = "ajaxList"
        }

        "/comment/list/$albumId/$photoId/feed/$feed" {
            controller = "comment"
            action = "list"
        }

        "/comment/ajaxList/$albumId/$photoId/feed/$feed" {
            controller = "comment"
            action = "ajaxList"
        }

        "/comment/list/feed/$feed" {
            controller = "comment"
            action = "list"
        }

        "/comment/ajaxList/feed/$feed" {
            controller = "comment"
            action = "ajaxList"
        }

        "/"(view:"/index")
        "500"(view:"/error")
    }
}
