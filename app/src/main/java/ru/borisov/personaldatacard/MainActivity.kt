package ru.borisov.personaldatacard

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var inputNameET: EditText
    private lateinit var inputSurnameET: EditText
    private lateinit var inputBirthDateET: EditText
    private lateinit var photoIV: ImageView
    private lateinit var saveBTN: Button
    private var photoUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initVariables()
        setSupportActionBar(toolbar)
        photoIV.setOnClickListener {
            startGetImageIntent()
        }
        saveBTN.setOnClickListener {
            onClickSave()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GALLERY_REQUEST_CODE -> if (resultCode == RESULT_OK) {
                photoUri = data?.data
                photoIV.setImageURI(photoUri)
            }
        }
    }

    private fun initVariables() {
        toolbar = findViewById(R.id.toolbar)
        inputNameET = findViewById(R.id.inputNameET)
        inputSurnameET = findViewById(R.id.inputSurnameET)
        inputBirthDateET = findViewById(R.id.inputBirthDateET)
        photoIV = findViewById(R.id.photoIV)
        saveBTN = findViewById(R.id.saveBTN)
    }

    private fun onClickSave() {
        try {
            val person = getPerson()
            val intent = Intent(this, CardActivity::class.java).apply {
                putExtra("person", person)
            }
            startActivity(intent)
            finish()
        } catch (e: ParseException) {
            Toast.makeText(this, "Неверный формат даты рождения", Toast.LENGTH_LONG).show()
        }
    }

    private fun getPerson(): Person {
        val name = inputNameET.text.toString()
        val surname = inputSurnameET.text.toString()
        val birthDateLong = getDateLong(inputBirthDateET.text.toString())
        val photo = photoUri?.toString()
        println("photo $photo")
        return Person(name, surname, birthDateLong, photo)
    }

    fun getDateLong(birthDateText: String): Long {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.parse(birthDateText)?.time ?: 0L
    }

    private fun startGetImageIntent() {
        val photoPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST_CODE)
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 302
    }
}