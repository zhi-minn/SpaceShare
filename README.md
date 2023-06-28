# SpaceShare

## Work Log

* Minn - 2 hours (2023-06-07) [Commit](https://github.com/jimmyfong0/SpaceShare/commit/f109ce53629aae603deb0c328f073f2243ca8684)
  + Setup navigation graph, activities, and fragments
  + Enable Firebase user login
* Minn - 1.5 hours (2023-06-11) [Commit](https://github.com/jimmyfong0/SpaceShare/commit/37615e8fb5807da3e3a38e14798c2ed30593bea4)
  + Added BottomNavigationView to navigate between core pages
  + Styled aforementioned view
* Chang - 2 hours (2023-06-12) [Commit 1](https://github.com/jimmyfong0/SpaceShare/commit/05d7b2810d8050da3f0b95ffc49db9d35c58ce94) [Commit 2](https://github.com/jimmyfong0/SpaceShare/commit/d74179e650e1ebb612b487eed324bc8b970b42ca)
  + Setup layout for create listing fragment
* Minn - 4.5 hours (2023-06-14) [Commit](https://github.com/jimmyfong0/SpaceShare/commit/143493acaf942af6fc6fb7b51004cbc120dc3c34)
  + Added and configured Hilt dependency injection
  + Made ViewPager2 adapter to enable user to swipe between space images
  + Fetch listing functionality now works on dummy data
  + TODO:
    * Configure User model to be dependency injected so it is accessible from any Fragment
    * Complete fetch listing functionality to fetch from dynamic user data instead of hard-coded ID
* Minn - 3 hours (2023-06-15) [PR](https://github.com/zhi-minn/SpaceShare/pull/1)
  + Includes above commit
  + Finally fixed BottomNavigationView not working when switching between modes (something related to navigation backstack)
* Chang 1.5 hours (2023-06-15) [PR](https://github.com/zhi-minn/SpaceShare/pull/2)
  + Added basic functionality to create a listing and add it to the Firestore database
  + Added publish functionality to ListingRepo
* Minn 8 hours (2023-06-16 & 2023-06-19) [PR](https://github.com/zhi-minn/SpaceShare/pull/4)
  + Integrated publish listing feature into app workflow
  + Imported and implemented a few libraries to improve UX (Image cropper and Image Carousel)
* Minn 2 hours (2023-06-20) [Commit](https://github.com/zhi-minn/SpaceShare/commit/afe103dff8eb470131625dc7ee3822b25fd18c17)
  + Add pop up features when click on image
  + Add margin between listings and made listing cards rounded to define a more polished UI
* Chang 0.25 hours (2023-06-21) [PR](https://github.com/zhi-minn/SpaceShare/pull/7)
  + Refined user flow by making image selector pop up immediately when clicking add photo button
* Calvin 8 hours (2023-06-21) [Commit](https://github.com/zhi-minn/SpaceShare/commit/d9a03400c663057542c95667aeaaf45530e48fda)
  + Configuration set up
  + Reservation Model + Repo + View Model
* Minn 4 hours (2023-06-22) [Commit](https://github.com/zhi-minn/SpaceShare/commit/bf48ab35e41d3e4afb1cc6b549f6f23f465c3f0b)
  * Integrated Google Maps SDK into create listing feature
  * Integrated Google Maps AutoComplete search into Maps
  * Integrated Google Maps moving marker to allow user to pinpoint location
* Minn 2 hours (2023-06-22) [Commit](https://github.com/zhi-minn/SpaceShare/commit/3f114b2e3dea51586535340a63d202420138b62a)
  + Prettify login and sign up page
* Chang 1 hour (2023-06-23) [PR](https://github.com/zhi-minn/SpaceShare/pull/10)
  + Add basic layout for search functionality 
* Minn 1.5 hours (2023-06-23) [Commit](https://github.com/zhi-minn/SpaceShare/commit/f6474ac38fe34e505611876f46784e3e57773eeb)
  + Prettify create listing fragment
  + Link location selected to create listing fragment and geocode LatLng into address
* Minn 3.5 hours (2023-06-23) [Commit](https://github.com/zhi-minn/SpaceShare/commit/99f26037052ae3d4a3b8ae67a5d3df1b11201ce4)
  + Added preferences page and setup relevant fragments and view models for persistence
* Minn 4 hours (2023-06-24) [Commit](https://github.com/zhi-minn/SpaceShare/commit/69bc5cbb51395490d0d53e8e012257a18876dc87)
  + Add location field to preferences and bind it to selected location on Google Maps SDK
  + Notify users by email when location matching preference posted
  + Smooth transition when publishing listing
  + Fix minor layout issues
* Wenyu 6.5 hours (2023-06-24) [Commit](https://github.com/zhi-minn/SpaceShare/commit/5dd65c145d37174ee481fb2a321a23c7dad34d33)
  + Add search function backend basic logic
* William 4 hours (2023-06-24) [Commit](https://github.com/zhi-minn/SpaceShare/commit/592a4a40a51642cbec2361ea6105ec8615ea6d90)
  + Fix navigation and some ui work
* Calvin 3 hours (2023-06-25) [Commit](https://github.com/zhi-minn/SpaceShare/commit/15e6bc9c17880f90c430241c6d606045ce01faea)
  + Reservation Fragment
  + Reservation ViewModel & Model update
* Youming 6 hours (2023-06-25) [Commit](https://github.com/zhi-minn/SpaceShare/commit/13b0d66d08eec3bb02302ae6908724874cec2452)
  + Add orders bot navigate and corresponding UI
* Chang 7.5 hours (2023-06-25) [PR](https://github.com/zhi-minn/SpaceShare/pull/12)
  * Made the search page UI functional
  * Added:
    * Google Maps Dialog for choosing a location (thanks Minn)
    * MUI DateRangePicker for choosing date range
    * Custom card to select space required in cubic metres
    * ViewModel to store it all
* Chang 1 hour (2023-06-26) [PR](https://github.com/zhi-minn/SpaceShare/pull/13)
  * Refined create listing layout hints to be more user friendly
  * Resolved search fragment crashing on app startup
* Minn 2 hours (2023-06-26) [PR](https://github.com/zhi-minn/SpaceShare/pull/14)
  + Added delete functionality to host listings
  + Note: Can not implement RecyclerView swipe to delete functionality. Issue because ViewPager2 within RecyclerView also needs to handle swipe events
* Chang 2.5 hours (2023-06-26) [PR](https://github.com/zhi-minn/SpaceShare/pull/16)
  * Refactored search functionality into listings repo
  * Made search button trigger search from SearchViewModel
* Minn 1.25 hours (2023-06-26) [PR](https://github.com/zhi-minn/SpaceShare/pull/18)
  * Added storage space available details to create listing fragment
  * Refactor fragment_create_listing to use `TextInputLayout` for better UI
* Calvin 4 hours (2023-06-26) [Commit](https://github.com/zhi-minn/SpaceShare/commit/4c30f5e337a0f55a84b901e4607df3ed86dc6a78)
  + Added display as card view functions into Fragment
  + Updated reservation_listing xml
  + Refactored Reservation Model on status logic
* Chang 3.5 hours (2023-06-27) [PR](https://github.com/zhi-minn/SpaceShare/pull/20)
  * Moved current search UI into search dialog
  * Added new search page (that uses the search dialog + displays results)
* Calvin 2 hours (2023-06-27) [PR](https://github.com/zhi-minn/SpaceShare/commit/22bf8fcea227888fbd5a0f84d3726c92953404e0)
  + Cleanup + UI update
* Minn 5 hours (2023-06-28)
  * [Commit](https://github.com/zhi-minn/SpaceShare/commit/5038764e2e16ca5c7027a66cc8c9114138d1809b) - Add filter for listing search
  * [Commit](https://github.com/zhi-minn/SpaceShare/commit/5629599f9af07d9e93f1035894103acd268ac9ff) - Refactor Geocoder to utility class
  * [Commit](https://github.com/zhi-minn/SpaceShare/commit/75b7a5488739899f25d92d053b0083ad8ba9dc5f) - Create dialog fragment for detailed host listing
* Chang 0.5 hours (2023-06-28) [PR](https://github.com/zhi-minn/SpaceShare/pull/26) [PR](https://github.com/zhi-minn/SpaceShare/pull/27) [PR](https://github.com/zhi-minn/SpaceShare/pull/28) [PR](https://github.com/zhi-minn/SpaceShare/pull/30)
  * Fix search dialog location being empty
  * Fix search dialog crashing app when cancel is clicked
  * Fix search not filtering for available space
* Chang 0.25 hours (2023-06-28) [PR](https://github.com/zhi-minn/SpaceShare/pull/32)
  * Added dismiss button to image popup 
