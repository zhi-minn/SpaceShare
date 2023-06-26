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
  + Added preferences page and setup relevant fragments and view models for persistences
* Minn 4 hours (2023-06-24) [Commit](https://github.com/zhi-minn/SpaceShare/commit/69bc5cbb51395490d0d53e8e012257a18876dc87)
  + Add location field to preferences and bind it to selected location on Google Maps SDK
  + Notify users by email when location matching preference posted
  + Smooth transition when publishing listing
  + Fix minor layout issues
* Wenyu 6.5 hours (2023-06-24) [Commit](https://github.com/zhi-minn/SpaceShare/commit/5dd65c145d37174ee481fb2a321a23c7dad34d33)
  + Add search function backend basic logic
* William 4 hours (2023-06-24) [Commit](https://github.com/zhi-minn/SpaceShare/commit/592a4a40a51642cbec2361ea6105ec8615ea6d90)
  + Fix navigation and some ui work
* Chang 1 hour (2023-06-25) [PR](https://github.com/zhi-minn/SpaceShare/pull/10)
  + Add basic layout for search functionality
* Youming 6 hours (2023-06-25) [Commit](https://github.com/zhi-minn/SpaceShare/commit/13b0d66d08eec3bb02302ae6908724874cec2452)
  + Add orders bot navigate and corrsbonging UI
* Chang 6 hours (2023-06-25) [PR](https://github.com/zhi-minn/SpaceShare/pull/12)
  + Add proper layout for search functionality
