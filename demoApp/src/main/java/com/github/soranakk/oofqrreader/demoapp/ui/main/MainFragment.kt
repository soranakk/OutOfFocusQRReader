package com.github.soranakk.oofqrreader.demoapp.ui.main

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.soranakk.oofqrreader.demoapp.R
import com.github.soranakk.oofqrreader.demoapp.databinding.MainFragmentBinding
import com.wada811.databinding.dataBinding
import org.opencv.core.Point

class MainFragment : Fragment(R.layout.main_fragment) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()
    private val binding: MainFragmentBinding by dataBinding()

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.reader_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.readerSpinner.adapter = adapter
        }

        binding.readerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                viewModel.readerName.value = (binding.readerSpinner.adapter.getItem(pos) as CharSequence).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        viewModel.decodeResult.observe(viewLifecycleOwner, Observer { (size, result) ->
            if (result != null) {
                binding.qrRect.visibility = View.VISIBLE
                drawRect(result.detectPoint, size.width, size.height)
            } else {
                binding.qrRect.visibility = View.INVISIBLE
            }
        })

        viewModel.startCamera(requireContext(), binding.qrReaderPreview, this)
    }

    private fun drawRect(detectPoint: List<Point>, width: Int, height: Int) {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = 5.0f
        }

        canvas.drawPath(Path().apply {
            detectPoint.forEachIndexed { index, point ->
                if (index == 0) moveTo(point.x.toFloat(), point.y.toFloat())
                else lineTo(point.x.toFloat(), point.y.toFloat())
            }
            close()
        }, paint)

        binding.qrRect.setImageBitmap(mutableBitmap)
    }
}
