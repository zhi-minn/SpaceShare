# SpaceShare

## Work Log
* Chang - 1.5 hours (2023-06-12) [Commit 1](https://github.com/jimmyfong0/SpaceShare/commit/05d7b2810d8050da3f0b95ffc49db9d35c58ce94) [Commit 2](https://github.com/jimmyfong0/SpaceShare/commit/d74179e650e1ebb612b487eed324bc8b970b42ca)
  + Setup layout for create listing fragment
* Minn - 2 hours (2023-06-07) [Commit](https://github.com/jimmyfong0/SpaceShare/commit/f109ce53629aae603deb0c328f073f2243ca8684)
  + Setup navigation graph, activities, and fragments
  + Enable Firebase user login
* Minn - 1.5 hours (2023-06-11) [Commit](https://github.com/jimmyfong0/SpaceShare/commit/37615e8fb5807da3e3a38e14798c2ed30593bea4)
  + Added BottomNavigationView to navigate between core pages
  + Styled aforementioned view
* Minn - 4.5 hours (2023-06-14) [Commit](https://github.com/jimmyfong0/SpaceShare/commit/143493acaf942af6fc6fb7b51004cbc120dc3c34)
  + Added and configured Hilt dependency injection
  + Made ViewPager2 adapter to enable user to swipe between space images
  + Fetch listing functionality now works on dummy data
  + TODO:
    * Merge Chang's publish listing functionality into ListingRepository
    * Add logout button
    * Configure User model to be dependency injected so it is accessible from any Fragment
    * Complete fetch listing functionality to fetch from dynamic user data instead of hard-coded ID
    * It's 1.32AM and I am tired
* Minn - 3 hours (2023-06-15) [PR](https://github.com/zhi-minn/SpaceShare/pull/1)
  + Includes above commit
  + Finally fixed BottomNavigationView not working when switching between modes (something related to navigation backstack)