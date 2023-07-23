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
* Minn 4 hours (2023-06-28) [PR](https://github.com/zhi-minn/SpaceShare/pull/33)
  * Added detailed host listing page
  * Added amenities option to listing
  * Added map view to detailed host listing page
* Chang 1.5 hours (2023-06-29) [PR](https://github.com/zhi-minn/SpaceShare/pull/34)
  * Added slider to search dialog to let user input search radius around the chosen location
  * Made search function in listing repo filter by distance
  * Made text in search dialog bigger for better UX
* Chang 0.5 hours (2023-06-29) [PR](https://github.com/zhi-minn/SpaceShare/pull/35)
  * Hide search radius related UI unless a location is set
  * Fix search not returning results when no location is set
* Minn 2.5 hours (2023-06-29) [PR](https://github.com/zhi-minn/SpaceShare/pull/38) [PR](https://github.com/zhi-minn/SpaceShare/pull/39)
  * Smooth UI update when add new listing
  * Add createdAt and updatedAt timestamps to Listing model
* Chang 2 hours (2023-06-29) [PR](https://github.com/zhi-minn/SpaceShare/pull/40)
  * Add detailed client listing page
* Minn 6 hours (2023-06-28 -> 2023-06-29) [PR](https://github.com/zhi-minn/SpaceShare/pull/41)
  * Host can now search and filter through own listings
  * Host can update listing status (active or inactive)
* Chang 2 hours (2023-06-29 -> 2023-07-10) 
  * Investigate detailed client listing page crashing when reaching end of ScrollView
    * Detailed client listing page does not crash when on hardware Android device, still crashes on emulator
    * Deprioritizing since the problem is not as severe as initially thought and has already taken up a lot of time
  * As of 2023-07-10, app no longer crashes on emulator, marking this as complete
* William 6 hours (2023-06-30) [Commit](https://github.com/zhi-minn/SpaceShare/commit/1b0cfcd1470ff970d7fbc90f7192de39ab7fff65)
  + User verification display, create user document to UserVerified collection when sign up
* Chang 2 hours (2023-06-30) [PR](https://github.com/zhi-minn/SpaceShare/pull/43)
  * Add filtering functionality to client searches
  * Added title and dismiss button to search dialog for better UI
  * Smoothed transition when opening dialogs on search page for better UX
* Chang 0.5 hours (2023-06-30) [PR](https://github.com/zhi-minn/SpaceShare/pull/44)
  * Fix search dialog opening instead of filter dialog when edge of filter button is clicked
  * Refine search UI by making touch targets larger and more intuitive
  * Add "$" to start of price labels for listings
* William 3 hours (2023-07-01) [Commit](https://github.com/zhi-minn/SpaceShare/commit/1799ae8285417a426bad71a6e971345dada1eefd)
  + Removed Firestore db from ProfileFragment and accessed it from UserViewModel
  + Add user to new collection "user" when signup with email and isVerified populated
* Minn 2.5 hours (2023-07-01) [PR](https://github.com/zhi-minn/SpaceShare/pull/47)
  * Implemented Room database for local data access
  * Fixed ProfileFragment to properly access User custom object from Repository layer
* Chang 3.5 hours (2023-07-01) [PR](https://github.com/zhi-minn/SpaceShare/pull/48)
  * Added Google SSO integration for login and signup
* Chang 0.75 hours (2023-07-01) [PR](https://github.com/zhi-minn/SpaceShare/pull/49/)
  * Refactor processing of Google SSO credentials
    * Moved processing logic out of LoginFragment and into AuthViewModel
    * Process Google credential user details separately from loginWithSSOCredentials function as different SSO providers may handle user details differently
  * Tested signup user flow with new Google user
* Minn 1 hour (2023-07-02) [PR](https://github.com/zhi-minn/SpaceShare/pull/50)
  * Bootstrap profile details page dialog
* Minn 4 hours (2023-07-03) [PR](https://github.com/zhi-minn/SpaceShare/pull/51)
  * Host can now edit listing, including adding pictures
  * Refactored ImageAdapter to use ImageModel class that can take either a FirebaseStorage filepath or local image URI
* Chang 1 hour (2023-07-03) [PR](https://github.com/zhi-minn/SpaceShare/pull/52)
  * Conducted user interviews, facilitating by giving testers tasks and noting down any difficulties they had
  * Revised create listing page based on observations and feedback
    * Made publish listing a bar button on the bottom of the page rather than an icon button (testers could not find how to publish)
    * Made "Please select a location" text also trigger the map dialog, users were not sure where to select a location and kept clicking the text which did not do anything
* Chang 1.75 hours (2023-07-05) [PR](https://github.com/zhi-minn/SpaceShare/pull/54)
  * Added price recommendations ("Smart Pricing") to create listing dialog
    * Moved location selector to the top of the dialog to facilitate smart pricing
    * Smart pricing is based on the average price of listings in a 5km radius around the location
  * Added "$" prefix to price input field
  * Refactored client search filters to apply in one go rather than filter sequentially
* Chang 0.75 hours (2023-07-05) [PR](https://github.com/zhi-minn/SpaceShare/pull/55)
  * Added no listings view for search page (prompts users to check their filters and search criteria)
  * Fixed edit button being available on search results
  * Removed cancel button on search dialog
* Chang 0.5 hours (2023-07-05) [PR](https://github.com/zhi-minn/SpaceShare/pull/56)
  * Made crop photo UI more intuitive
  * Fixed recommended price displaying NaN and limited it to 2 demical digits
* Minn 1 hour (2023-07-07) [Commit](https://github.com/zhi-minn/SpaceShare/commit/79af49cbded77ef8705d5e76980ceed4e667ead9)
  * Added placeholder image for uploading listing
  * Add validation for uploading image
  * Scroll to uploaded image when adding pictures
* Minn 0.75 hour (2023-07-07) [Commit](https://github.com/zhi-minn/SpaceShare/commit/df0c2cfcba62998fb750e43dfae28a56393bbd2f)
  * Add delete image functionality to listing creation or update
* Minn 3 hours (2023-07-08) [Commit](https://github.com/zhi-minn/SpaceShare/commit/6d3717438848de9aa1f6166cc7dbab3a4587da6a)
  * Implement update user metadata functionality
* William 1 hour (2023-07-08) [Commit](https://github.com/zhi-minn/SpaceShare/commit/62ace157f15aa2207af5ede87ef46c99550d46fd)
  + Fix back button on profile dialog fragment
  + Add initial UI government ID part in profile details
* William 1.5 hour (2023-07-09) [Commit](https://github.com/zhi-minn/SpaceShare/commit/66c3c34605e3e21e435cb51a3b7c173606fe7d80)
  + Government ID edit profile screen UI
* Youming 1.5 hour (2023-07-13) [Commit](https://github.com/zhi-minn/SpaceShare/commit/2651dd024918ead1e2ac4b72866a43469a857269)
  + Reservation detailed page
* Chang 1.5 hours (2023-07-14) [PR](https://github.com/zhi-minn/SpaceShare/pull/60)
  * Added ability to filter by amenities to search page
* Minn 4 hours (2023-07-14) [PR](https://github.com/zhi-minn/SpaceShare/pull/61)
  * Added push notifications and cron job to trigger it (Firebase Cloud Messaging)
* William 10 hours (2023-07-11 -> 2023-07-14) [Commit](https://github.com/zhi-minn/SpaceShare/commit/d502d395e2b746da970e36fc3697fbb646b378b2)
  + Government id screen ui improvements
  + Photo picker to select photo
  + Upload government Id to Firebase Storage feature
* Youming Ning 8 hours (2023-07-15 -> 2023-07-17) [Commit](https://github.com/zhi-minn/SpaceShare/commit/db6964aa3b85692a20c00e3adb3a7adc91df8bf5)
  + Add Reservation Page source file
  + modified Detailed page for orders of the client
* Chang 6 hours (2023-07-16) [PR](https://github.com/zhi-minn/SpaceShare/pull/64)
  * Added messaging functionality
    * Users can send text and image messages
    * TODO:
      * Make channels so not all users talk on the same channel lol
* Chang 1.5 hours (2023-07-16) [PR](https://github.com/zhi-minn/SpaceShare/pull/65)
  * Fixed user profile photos not showing up in messages
  * Make image loading gif higher quality
* Calvin 5 hours (2023-07-16) [Commit](https://github.com/zhi-minn/SpaceShare/commit/ae514677aef3ac8ade890ccc64418ae5e5e18fa0)
  + Created Reservation Detail view on Orders page
  + Created Reservation Detail Fragment
  + Updated Orders Page to include Detail view binding
  + Updated Reservation model
* Chang 9 hours (2023-07-17) [PR](https://github.com/zhi-minn/SpaceShare/pull/66)
  * Added timestamp to each message
  * Added individual chats/channels to messaging service
  * Added ability for prospective clients to message host from listing details page
    * Will not create duplicate chats for the same listing
  * Messages fragment now shows all chats a user is part of
    * Chats keep track of last update
  * Revamped old messages fragment into reuseable chat dialog fragment
* Chang 2 hours (2023-07-17) [PR](https://github.com/zhi-minn/SpaceShare/pull/67)
  * Sorted chats by most recently updated in messages tab
  * Fixed image messages not having a timestamp
  * Refactored image loading into utility class
  * Added host names to chats
  * Added chat pictures (uses first image of listing)
* Calvin 3 hours (2023-07-17) [Commit](https://github.com/zhi-minn/SpaceShare/commit/ab85bfd268d9653113671ea18143396941691422)
  + Added fetchListings logic into ReservationRepoImpl
  + Updated ReservationViewModel and integrated fetchListing logic
  + Updated Reservation Detail Page layout
* Chang 2 hours (2023-07-18) [PR](https://github.com/zhi-minn/SpaceShare/pull/69)
  * Made chat message timestamps more concise
  * Chats now refresh the chats list in the messages tab upon being closed or when a message is sent
  * Chat last update time now uses "Just now" instead of "0 minutes ago"
* William 12.5 hours (2023-07-14 -> 2023-07-18) [Commit](https://github.com/zhi-minn/SpaceShare/commit/26d935cbcb7746c07d1d16cdce2a97a4dce7ccb6)
  + New admin activity that has recycler view of government id requests
  + Can click on any request to go to fragment containing details of users
  + Approve or reject user government id request and updates verified field in Firebase
  + Update status on client side depending on verified field
* Wenyu 2 Hours (2023-7-19) [Commit](https://github.com/zhi-minn/SpaceShare/commit/d820b39b0154d15e1c6ee3f9785bf97b055ee680)
  + Item Declaration Page initialization
* William 3 hours (2023-07-19) [Commit](https://github.com/zhi-minn/SpaceShare/commit/5f5de64c13a66011d1f2ff02f3020faba8283026)
  + UI changes on admin fragment
  + Back button on user entry fragment
* Youming 10 hours (2023-07-19) [Commit](https://github.com/zhi-minn/SpaceShare/commit/40cb77c553e76418b82d5f57aba550f188bb06d6) [Commit](https://github.com/zhi-minn/SpaceShare/commit/40cb77202c9ab66d35f609184304582bb9c93052bcfc8a) [Commit](https://github.com/zhi-minn/SpaceShare/commit/d54d2b602d33bfbb344bdad1ef87dcabcac0b89f)
  + Finish reservation function with Calvin
  + Implement the confirmation page and complete detailed add-ons for reservation
* Calvin 3 hours (2023-07-19) [Commit](https://github.com/zhi-minn/SpaceShare/commit/c38d1fe5e6c5505374596fc7d5a4d1f20d81d789)
  + Completed reserveListing logic
  + Modified fragment to integrate reserve function
* Chang 5 hours (2023-07-19) [PR](https://github.com/zhi-minn/SpaceShare/pull/71)
  * Made search functionality only show listings that are available (have enough space to accommodate the request, factoring in existing bookings)
  * Updated SearchViewModel to use MutableLiveData and LiveData
  * Made search dialog and search filter dialog access parent SearchViewModel correctly
    * Fixes said dialogs not persisting state between searches and filters
  * Fixed filter dialog crashing in a wide range of circumstances
  * Fixed filter dialog slider text formatting
* Chang 0.5 hours (2023-07-20) [PR](https://github.com/zhi-minn/SpaceShare/pull/74)
  * Made reservations add to a listing's bookings
* Chang 1.25 hours (2023-07-20) [PR](https://github.com/zhi-minn/SpaceShare/pull/75)
  * Added listing previews to chats
* Chang 0.5 hours (2023-07-20) [PR](https://github.com/zhi-minn/SpaceShare/pull/76)
  * Designed new app icon
* Chang 3.25 hours (2023-07-20) [PR](https://github.com/zhi-minn/SpaceShare/pull/77) [PR](https://github.com/zhi-minn/SpaceShare/pull/79)
  * Add sort options to client filter
* Chang 0.5 hours (2023-07-20) [PR](https://github.com/zhi-minn/SpaceShare/pull/78)
  * Clicking on the listing preview in chat now gives you a full listing details popup
* Wenyu 3 hours (2023-07-20) [PR](https://github.com/zhi-minn/SpaceShare/commit/b1738a33d93c8c2a9e032fff2f2516255ffca772) [PR](https://github.com/zhi-minn/SpaceShare/commit/699a14754b977e886a6f9bb4d4685609b3ec6f1d)
  * Add item declaration button
  * Add basic structure for item Declaration
* Chang 3 hours (2023-07-21) [PR](https://github.com/zhi-minn/SpaceShare/pull/80)
  * Made recommended pricing factor in amount of space available
  * Added chat creation as default first update for chats
  * Added scroll to top floating action button for search
  * Made search page scroll to top after applying filters (most noticeable when sort order is changed)
* Wenyu 3 hours (2023-07-21) [PR](https://github.com/zhi-minn/SpaceShare/commit/12ab55c9133d1d8e7cbbae0128ac34ac18dfce29)
  * Update item declaration Basic UI
* William 1 hours (2023-07-21) [Commit](https://github.com/zhi-minn/SpaceShare/commit/91651a5361979510b72b0aac3ae486a208928c9c)
  + Update UI for UserEntry
  + Toast message on accept or reject
* William 3 hours (2023-07-21) [Commit](https://github.com/zhi-minn/SpaceShare/commit/083f9b8425b940cb3584617307a6e35355b42ff0)
  + Move admin accounts check to Firebase
* William 2 hours (2023-07-22) [Commit](https://github.com/zhi-minn/SpaceShare/commit/9c928623fff812692862664ade0f7fadb9394ff1)
  + Add toast message for successful id submission
  + Change drawable images depending on id status
* William 3 hours (2023-07-23) [Commit](https://github.com/zhi-minn/SpaceShare/commit/b2488084e57eda39b38c04703bee1819ec0bcdf7)
  + Add user email to UserEntry display
* William 2 hours (2023-07-23) [Commit](https://github.com/zhi-minn/SpaceShare/commit/925948a7df7e562980668633361e2cca976ad69d)
  + Add ID status on client listing
* Minn 2 hours (2023-07-23) [Commit](https://github.com/zhi-minn/SpaceShare/pull/87)
  * Added revenue analytics