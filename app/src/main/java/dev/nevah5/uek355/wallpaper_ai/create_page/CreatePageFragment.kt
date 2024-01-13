package dev.nevah5.uek355.wallpaper_ai.create_page

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.nevah5.uek355.wallpaper_ai.R

class CreatePageFragment : Fragment() {

    companion object {
        fun newInstance() = CreatePageFragment()
    }

    private lateinit var viewModel: CreatePageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreatePageViewModel::class.java)
        // TODO: Use the ViewModel
    }

}