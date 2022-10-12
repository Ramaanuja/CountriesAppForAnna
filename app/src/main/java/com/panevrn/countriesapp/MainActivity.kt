package com.panevrn.countriesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.panevrn.countriesapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
//    val viewModel = MainActivityViewModel()
    val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setObservers()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.searchButton.setOnClickListener {
            val countryName = binding.countryNameEditText.text.toString()

            viewModel.onSearchButtonClick(countryName)
        }
    }

    fun setObservers(){
        viewModel.countryState.observe (this){ country->
            if(country != null)
                showCountryInfo(country)
        }

        viewModel.progressBarIsVisible.observe (this){isVisible->
            if(isVisible)
                showProgressBar()
            else
                hideProgressBar()
        }

        viewModel.statusLayoutIsVisible.observe (this){isVisible->
            if(isVisible)
                showStatusLayout()
            else
                hideStatusLayout()
        }
    }

    fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
    }

    fun hideProgressBar(){
        binding.progressBar.visibility = View.GONE
    }

    fun hideStatusLayout(){
        binding.resultLayout.visibility = View.VISIBLE
        binding.statusLayout.visibility = View.INVISIBLE
    }

    fun showStatusLayout(){
        binding.resultLayout.visibility = View.INVISIBLE
        binding.statusLayout.visibility = View.VISIBLE
    }

    fun showCountryInfo(country: Country){
        binding.countryNameTextView.text = country.name
        binding.capitalTextView.text = country.capital
        binding.populationTextView.text = formatNumber(country.population)
        binding.areaTextView.text = formatNumber(country.area)
        binding.languagesTextView.text = languagesToString(country.languages)
        showFlag(country.flag)
    }

    fun showFlag(flagUrl: String){
        lifecycleScope.launch {
            loadSvg(binding.imageView, flagUrl)
        }
    }
}

class MainActivityViewModel: ViewModel(){
    var progressBarIsVisible = MutableLiveData<Boolean>(false)
    var countryState = MutableLiveData<Country>(null)
    var statusLayoutIsVisible = MutableLiveData<Boolean>(true)

    fun onSearchButtonClick(countryName: String){
        GlobalScope.launch(Dispatchers.Main) {
            progressBarIsVisible.value = true

            val countries = restCountriesApi.getCountryByName(countryName)
            val country = countries[0]
            countryState.value = country

            progressBarIsVisible.value = false


            statusLayoutIsVisible.value = false
        }
    }
}



