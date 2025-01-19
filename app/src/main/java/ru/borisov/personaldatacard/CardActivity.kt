package ru.borisov.personaldatacard

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class CardActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var outputNameTV: TextView
    private lateinit var outputSurnameTV: TextView
    private lateinit var outputAgeTV: TextView
    private lateinit var outputDayMonthToNextBirthDayTV: TextView
    private lateinit var photoIV: ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        initVariables()
        setSupportActionBar(toolbar)
        val person = intent.parcelable<Person>("person")
        person?.apply {
            val bdLocalDate =
                Instant.ofEpochMilli(birthDate).atZone(ZoneId.systemDefault()).toLocalDate()
            val now = LocalDate.now()
            val age = bdLocalDate.until(now, ChronoUnit.YEARS)
            val nextBirthDay =
                if (now.dayOfYear <= bdLocalDate.dayOfYear) bdLocalDate.withYear(now.year)
                else bdLocalDate.withYear(now.year + 1)
            val monthsLeft = now.until(nextBirthDay, ChronoUnit.MONTHS)
            val daysLeft = (now.plusMonths(monthsLeft)).until(nextBirthDay, ChronoUnit.DAYS)
            bindPersonData(age, monthsLeft, daysLeft)
        }
    }

    private fun Person.bindPersonData(
        age: Long,
        monthsLeft: Long,
        daysLeft: Long,
    ) {
        outputNameTV.text = resources.getString(R.string.name, name)
        outputSurnameTV.text = resources.getString(R.string.surname, surname)
        outputAgeTV.text = resources.getString(R.string.age, age)
        outputDayMonthToNextBirthDayTV.text = resources.getString(
            R.string.before_next_birth_day,
            monthsLeft,
            daysLeft
        )
        photo?.let {
            photoIV.setImageURI(Uri.parse(photo))
        }
    }

    private fun initVariables() {
        toolbar = findViewById(R.id.toolbar)
        outputNameTV = findViewById(R.id.outputNameTV)
        outputSurnameTV = findViewById(R.id.outputSurnameTV)
        outputAgeTV = findViewById(R.id.outputAgeTV)
        outputDayMonthToNextBirthDayTV = findViewById(R.id.outputDayMonthToNextBirthDayTV)
        photoIV = findViewById(R.id.photoIV)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_card_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.exit) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? =
        when {
            SDK_INT >= 33 -> getParcelableExtra<T>(key, T::class.java)
            else -> @Suppress("DEPRECATION") getParcelableExtra(key)
        }
}