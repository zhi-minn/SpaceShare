import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.interfaces.LocationInterface
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class MapDialogFragment(
    private val locationInterface: LocationInterface
) : DialogFragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mapFragment: SupportMapFragment? = null
    private var isLocationPermissionGranted: Boolean = false
    private var selectedLocationMarker: Marker? = null
    private var selectedLocation: LatLng? = null
    private var handler: Handler? = null
    // Search bar
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private var selectedPlace: Place? = null
    // Views
    private lateinit var btnConfirmLocation: ImageButton
    // Consts
    private val KW_MIN_LAT = 43.3474
    private val KW_MAX_LAT = 43.5709
    private val KW_MIN_LONG = -80.6480
    private val KW_MAX_LONG = -80.3532

    companion object {
        private val TAG = this::class.simpleName
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_map, container, false)

        // Initialize google maps
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        configureAutoCompleteFragment()

        // Initialize views
        btnConfirmLocation = view.findViewById(R.id.btnConfirmLocation)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureButtons()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(requireContext())
        Places.initialize(requireContext(), getApiKeyFromManifest(requireContext()))
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapFragment?.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        mapFragment?.onViewStateRestored(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapFragment?.onActivityCreated(savedInstanceState)
    }

    private fun configureButtons() {
        btnConfirmLocation.setOnClickListener {
            val location = selectedLocation
            if (location != null) {
                locationInterface.setLocation(location)
            }
            dialog?.dismiss()
        }
    }

    private fun configureAutoCompleteFragment() {
        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocompleteFragment)
                as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }
        })
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionGranted = true
            googleMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun configureSearchBar() {
        // Create a new Places client instance.
        val placesClient = Places.createClient(requireContext())

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocompleteFragment) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
        )

        autocompleteFragment?.setActivityMode(AutocompleteActivityMode.FULLSCREEN)
        val bounds = LatLngBounds(
            LatLng(KW_MIN_LAT, KW_MIN_LONG),
            LatLng(KW_MAX_LAT, KW_MAX_LONG)
        )
        autocompleteFragment?.setLocationBias(RectangularBounds.newInstance(bounds))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                selectedPlace = place
                place.latLng?.let { latLng ->
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    selectedLocation = latLng
                    updateMarkerPosition()
                }
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${status.statusMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun configureMapListeners() {
        googleMap.setOnCameraMoveListener {
            selectedLocation = googleMap.cameraPosition.target
            updateMarkerPosition()
        }

        googleMap.setOnCameraIdleListener {
            selectedLocation = googleMap.cameraPosition.target
            updateMarkerPosition()
        }
    }

    private fun zoomToUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (isLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Request location permission again
                return
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val latLng = LatLng(location.latitude, location.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    selectedLocation = latLng
                    updateMarkerPosition()
                }
            }.addOnFailureListener { exception: Exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to get location: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateMarkerPosition() {
        selectedLocation?.let { location ->
            if (selectedLocationMarker == null) {
                selectedLocationMarker = googleMap.addMarker(
                    MarkerOptions()
                        .position(location)
                )
            } else {
                handler?.removeCallbacksAndMessages(null)
                handler = Handler()
                val startPosition = selectedLocationMarker?.position
                val duration = 100 // Duration in milliseconds
                val startTime = System.currentTimeMillis()

                handler?.post(object : Runnable {
                    override fun run() {
                        val elapsed = System.currentTimeMillis() - startTime
                        val t = elapsed.toFloat() / duration.toFloat()
                        val lat = startPosition?.latitude?.plus((location.latitude - startPosition.latitude) * t)
                        val lng = startPosition?.longitude?.plus((location.longitude - startPosition.longitude) * t)

                        val newPosition = LatLng(lat!!, lng!!)
                        selectedLocationMarker?.position = newPosition

                        if (t < 1f) {
                            // If animation is not finished, post this runnable again
                            handler?.post(this)
                        }
                    }
                })
            }
        }
    }

    private fun getApiKeyFromManifest(context: Context): String {
        try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA
            )
            val bundle = appInfo.metaData
            return bundle.getString("com.google.android.geo.API_KEY", "")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

    override fun onResume() {
        super.onResume()
        mapFragment?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapFragment?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapFragment?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapFragment?.onLowMemory()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        enableMyLocation()
        configureSearchBar()
        configureMapListeners()
        zoomToUserLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission denied.",
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }
    }
}