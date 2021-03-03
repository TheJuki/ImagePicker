package com.github.dhaval2404.imagepicker.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.sample.databinding.ActivityMainBinding
import com.github.dhaval2404.imagepicker.sample.util.FileUtil
import com.github.dhaval2404.imagepicker.sample.util.IntentUtil
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {

        private const val GITHUB_REPOSITORY = "https://github.com/Dhaval2404/ImagePicker"

        private const val PROFILE_IMAGE_REQ_CODE = 101
        private const val GALLERY_IMAGE_REQ_CODE = 102
        private const val CAMERA_IMAGE_REQ_CODE = 103
    }

    private lateinit var binding: ActivityMainBinding
    private var mCameraFile: File? = null
    private var mGalleryFile: File? = null
    private var mProfileFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)
        binding.main.contentProfile.imgProfile.setDrawableImage(R.drawable.ic_person, true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_github -> {
                IntentUtil.openURL(this, GITHUB_REPOSITORY)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun pickProfileImage(view: View) {
        ImagePicker.with(this)
            // Crop Square image
            .cropSquare()
            // Image resolution will be less than 512 x 512
            .maxResultSize(512, 512)
            .start(PROFILE_IMAGE_REQ_CODE)
    }

    fun pickGalleryImage(view: View) {
        ImagePicker.with(this)
            // Crop Image(User can choose Aspect Ratio)
            .crop()
            // User can only select image from Gallery
            .galleryOnly()
            // Image resolution will be less than 1080 x 1920
            .maxResultSize(1080, 1920)
            .start(GALLERY_IMAGE_REQ_CODE)
    }

    fun pickCameraImage(view: View) {
        ImagePicker.with(this)
            // User can only capture image from Camera
            .cameraOnly()
            // Image size will be less than 1024 KB
            .compress(1024)
            .start(CAMERA_IMAGE_REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.e("TAG", "Path:${ImagePicker.getFilePath(data)}")
            // File object will not be null for RESULT_OK
            val file = ImagePicker.getFile(data)!!
            when (requestCode) {
                PROFILE_IMAGE_REQ_CODE -> {
                    mProfileFile = file
                    binding.main.contentProfile.imgProfile.setLocalImage(file, true)
                }
                GALLERY_IMAGE_REQ_CODE -> {
                    mGalleryFile = file
                    binding.main.contentGalleryOnly.imgGallery.setLocalImage(file)
                }
                CAMERA_IMAGE_REQ_CODE -> {
                    mCameraFile = file
                    binding.main.contentCameraOnly.imgCamera.setLocalImage(file, false)
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    fun showImageCode(view: View) {
        val resource = when (view) {
            binding.main.contentProfile.imgProfileCode -> R.drawable.img_profile_code
            binding.main.contentCameraOnly.imgCameraCode -> R.drawable.img_camera_code
            binding.main.contentGalleryOnly.imgGalleryCode -> R.drawable.img_gallery_code
            else -> 0
        }
        ImageViewerDialog.newInstance(resource).show(supportFragmentManager, "")
    }

    fun showImage(view: View) {
        val file = when (view) {
            binding.main.contentProfile.imgProfile -> mProfileFile
            binding.main.contentCameraOnly.imgCamera -> mCameraFile
            binding.main.contentGalleryOnly.imgGallery -> mGalleryFile
            else -> null
        }

        file?.let {
            IntentUtil.showImage(this, file)
        }
    }

    fun showImageInfo(view: View) {
        val file = when (view) {
            binding.main.contentProfile.imgProfileInfo -> mProfileFile
            binding.main.contentCameraOnly.imgCameraInfo -> mCameraFile
            binding.main.contentGalleryOnly.imgGalleryInfo -> mGalleryFile
            else -> null
        }

        AlertDialog.Builder(this)
            .setTitle("Image Info")
            .setMessage(FileUtil.getFileInfo(file))
            .setPositiveButton("Ok", null)
            .show()
    }
}
