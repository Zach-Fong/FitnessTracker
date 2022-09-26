package com.cmpt362.zachary_fong_fitnesstracker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_profile.*
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.zachary_fong_fitnesstracker.R
import java.io.File


class ProfileActivity : AppCompatActivity() {
    private lateinit var imgUri:Uri
    private lateinit var tmpImgUri: Uri
    private val imgFileName="user_img.jpg"
    private val tmpImgFileName="tmp_user_img.jpg"
    private lateinit var imageView: ImageView
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var myViewModel: MyViewModel
    private lateinit var nameText: EditText
    private lateinit var emailText: EditText
    private lateinit var phoneNumberText: EditText
    private lateinit var gradText: EditText
    private lateinit var majorText: EditText
    private lateinit var genderRadio: RadioGroup
    private var name = ""
    private var email = ""
    private var phoneNumber = ""
    private var grad = ""
    private var major = ""
    private var gender = 0
    private lateinit var sharedPref: SharedPreferences
    private lateinit var toast: Toast
    private lateinit var edit:SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
//        Util.checkPermissions(this)

        //xml objects and shared preferences
        nameText = findViewById(R.id.name)
        emailText = findViewById(R.id.email)
        phoneNumberText = findViewById(R.id.phoneNumber)
        gradText = findViewById(R.id.classGraduation)
        majorText = findViewById(R.id.major)
        genderRadio = findViewById(R.id.gender)
        sharedPref = getSharedPreferences("profile", Context.MODE_PRIVATE)
        edit = sharedPref.edit()
        var checkName = sharedPref.getString("name", "")
        var checkEmail = sharedPref.getString("email", "")
        var checkPhoneNumber = sharedPref.getString("phoneNumber", "")
        var checkGrad = sharedPref.getString("grad", "")
        var checkMajor = sharedPref.getString("major", "")
        var checkGender = sharedPref.getInt("gender", 0)
        //initializing xml objects
        checkName?.let{name = checkName}
        checkEmail?.let{email = checkEmail}
        checkPhoneNumber?.let{phoneNumber = checkPhoneNumber}
        checkGrad?.let{grad = checkGrad}
        checkMajor?.let{major = checkMajor}
        checkGender?.let{gender = checkGender}
        nameText.setText(name)
        emailText.setText(email)
        phoneNumberText.setText(phoneNumber)
        gradText.setText(grad)
        majorText.setText(major)
        genderRadio.check(gender)

        toast = Toast.makeText(this@ProfileActivity, "", Toast.LENGTH_SHORT)

//        //imageview from xml
//        imageView = findViewById(R.id.image_view)
//        //file object of image we are using
//        val imgFile = File(getExternalFilesDir(null), imgFileName)
//        val tmpImgFile = File(getExternalFilesDir(null), tmpImgFileName)
//        //uri for image
//        imgUri = FileProvider.getUriForFile(this, "com.cmpt362.zachary_fong_fitnesstracker", imgFile)
//        tmpImgUri = FileProvider.getUriForFile(this, "com.cmpt362.zachary_fong_fitnesstracker", tmpImgFile)
//        //updates image on picture taken
//        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
//        myViewModel.userImg.observe(this){
//            val bitmap = Util.getBitmap(this, tmpImgUri)
//            imageView.setImageBitmap(bitmap)
//        }
//        if(imgFile.exists()){
//            val bitmap = Util.getBitmap(this, imgUri)
//            imageView.setImageBitmap(bitmap)
//        }
//        //handling new image
//        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                it: ActivityResult ->
//            if(it.resultCode == Activity.RESULT_OK){
//                val bitmap = Util.getBitmap(this, tmpImgUri)
//                myViewModel.userImg.value = bitmap
//            }
//        }

//        img_pick_btn.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
//                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
//                    requestPermissions(permissions, PERMISSION_CODE);
//                }
//                else{
//                    selectFromGallery();
//                }
//            }
//            else{
//                //os earlier than marshmallow
//            }
//        }
    }

//    fun onChangePhotoClicked(view: View){
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, tmpImgUri)
//        cameraResult.launch(intent)
//    }

    fun onCancelClicked(view: View){
        val tmpImgFile = File(getExternalFilesDir(null), tmpImgFileName)
        if(tmpImgFile.exists()){
            tmpImgFile.delete()
        }

        toast?.let{toast.cancel()}
        toast = Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }

    fun onSaveClicked(view: View){
        name = nameText.text.toString()
        edit.putString("name", name)
        edit.commit()

        email = emailText.text.toString()
        edit.putString("email", email)
        edit.commit()

        phoneNumber = phoneNumberText.text.toString()
        edit.putString("phoneNumber", phoneNumber)
        edit.commit()

        grad = gradText.text.toString()
        edit.putString("grad", grad)
        edit.commit()

        gender = genderRadio.checkedRadioButtonId
        edit.putInt("gender", gender)
        edit.commit()

        val imgFile = File(getExternalFilesDir(null), imgFileName)
        val tmpImgFile = File(getExternalFilesDir(null), tmpImgFileName)

        if(imgFile.exists() && tmpImgFile.exists()){
            imgFile.delete()
            tmpImgFile.renameTo(imgFile)
        }
        else if(!imgFile.exists() && tmpImgFile.exists()){
            tmpImgFile.renameTo(imgFile)
        }

        toast?.let{toast.cancel()}
        toast = Toast.makeText(this, "Updated Profile", Toast.LENGTH_SHORT)
        toast.show()
        finish()
    }

    //contains intent for selecting image
//    fun selectFromGallery(){
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, 2)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(data != null){
                tmpImgUri = data.data!!
                val bitmap = Util.getBitmap(this, tmpImgUri)
                myViewModel.userImg.value = bitmap
            }
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when(requestCode){
//            PERMISSION_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    selectFromGallery()
//                }
//                else{
//                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    companion object {
        private val IMAGE_PICK_MODE = 1000;
        private val PERMISSION_CODE = 1001;
    }
}