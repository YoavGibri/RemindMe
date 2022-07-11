package com.hirshler.remindme.ui.infosplash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.hirshler.remindme.R
import com.hirshler.remindme.databinding.FragmentImageBinding

class ImageFragment(private val callback: (() -> Unit)? = null) : Fragment() {
    private lateinit var binding: FragmentImageBinding

    companion object {
        private const val IMAGE_RES: String = "image_res"

        fun newInstance(@DrawableRes image: Int? = null, callback: (() -> Unit)? = null): ImageFragment {
            return ImageFragment(callback).apply {
                arguments = Bundle().apply {
                    image?.let {
                        putInt(IMAGE_RES, it)
                    }
                }

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.getInt(IMAGE_RES)?.let { imageRes ->
            binding.image.setImageDrawable(ResourcesCompat.getDrawable(resources, imageRes, requireActivity().theme))

            when (imageRes) {
                R.drawable.splash1 -> binding.button.text = "Next"
                R.drawable.splash2 -> binding.button.text = "Done"
            }
        }

        binding.button.setOnClickListener {
            callback?.invoke()
        }
    }

}
