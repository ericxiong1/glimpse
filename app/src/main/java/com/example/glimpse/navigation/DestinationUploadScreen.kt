package com.example.glimpse.navigation
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import androidx.navigation.NavController
import com.mapbox.common.MapboxOptions
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.SearchResultsView
import androidx.lifecycle.lifecycleScope
import com.example.glimpse.BuildConfig
import com.example.glimpse.SharedViewModel
import com.mapbox.search.ui.view.DistanceUnitType
import kotlinx.coroutines.launch

/*
    FR16 - Get.Destination
    Form to enter the desired destination
    The contents of this file were derived from the Mapbox documentation sample code: https://docs.mapbox.com/android/search/examples/place-autocomplete-ui/
 */

@Composable
fun DestinationUploadScreen(navController: NavController, sharedViewModel: SharedViewModel) {

    MapboxOptions.accessToken = BuildConfig.MAPBOX_DOWNLOADS_TOKEN

    val placeAutocomplete = PlaceAutocomplete.create()
    var placeAutocompleteUiAdapter: PlaceAutocompleteUiAdapter
    var ignoreNextQueryUpdate = false
    var destination = sharedViewModel.destination

    // Fetch themed colors
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val linearLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER_HORIZONTAL
                setBackgroundColor(backgroundColor.toArgb())
            }

            val screenWidth = context.resources.displayMetrics.widthPixels
            val elementWidth = (screenWidth * 0.9).toInt()

            // Typing destination
            val editText = EditText(context).apply {
                hint = "Type your destination here"
                layoutParams = LinearLayout.LayoutParams(elementWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
                setText(destination?.name?: "")
                setPadding(16, 18, 0, 21)
                setTextColor(textColor.toArgb())
                setHintTextColor(textColor.copy(alpha = 0.6f).toArgb())
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 24f
                    setColor(primaryColor.copy(alpha = 0.2f).toArgb())
                }
            }

            // Spacer
            val spacer = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(elementWidth, 32)
            }

            // Destination confirmation button
            val confirmButton = Button(context).apply {
                text = "Confirm"
                layoutParams = LinearLayout.LayoutParams(elementWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
                    .apply{ bottomMargin = 21 }
                isVisible = (destination != null)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 24f
                    setColor(primaryColor.toArgb())
                }
                setTextColor(onPrimaryColor.toArgb())
                setOnClickListener {
                    sharedViewModel.destination = destination
                    navController.popBackStack()
                }
            }

            val clearButton = Button(context).apply {
                text = "Clear Destination"
                layoutParams = LinearLayout.LayoutParams(elementWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
                isVisible = (destination != null)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 24f
                    setColor(primaryColor.toArgb())
                }
                setTextColor(onPrimaryColor.toArgb())
                setOnClickListener {
                    sharedViewModel.destination = null
                    editText.setText("")
                    isVisible = false
                }
            }

            // Suggestions panel
            val searchResultsView = SearchResultsView(context)

            searchResultsView.initialize(
                SearchResultsView.Configuration(commonConfiguration = CommonSearchViewConfiguration(DistanceUnitType.METRIC))
            )

            placeAutocompleteUiAdapter = PlaceAutocompleteUiAdapter(
                view = searchResultsView,
                placeAutocomplete = placeAutocomplete
            )

            placeAutocompleteUiAdapter.addSearchListener(object: PlaceAutocompleteUiAdapter.SearchListener {
                override fun onSuggestionsShown(suggestions: List<PlaceAutocompleteSuggestion>) { }
                override fun onPopulateQueryClick(suggestion: PlaceAutocompleteSuggestion) { }
                override fun onError(e: Exception) { }
                override fun onSuggestionSelected(suggestion: PlaceAutocompleteSuggestion) {
                    destination = suggestion
                    editText.setText(destination!!.name)
                    searchResultsView.isVisible = false
                    confirmButton.isVisible = true
                    hideKeyboard(editText, context)
                }
            })

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun afterTextChanged(s: Editable) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    confirmButton.isVisible = false
                    if (s.toString() != destination?.name) {
                        if (ignoreNextQueryUpdate) {
                            ignoreNextQueryUpdate = false
                        }

                        val lifecycleOwner = context as? androidx.lifecycle.LifecycleOwner
                        lifecycleOwner?.lifecycleScope?.launch {
                            placeAutocompleteUiAdapter.search(s.toString())

                            if (s != null) {
                                searchResultsView.isVisible = s.isNotEmpty()
                            }
                        }
                    }
                }
            })

            linearLayout.addView(editText)
            linearLayout.addView(searchResultsView)
            linearLayout.addView(spacer)
            linearLayout.addView(confirmButton)
            linearLayout.addView(clearButton)
            linearLayout
        }
    )
}

fun hideKeyboard(view: View, context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}