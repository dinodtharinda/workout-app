package edu.hardwork.workoutapp

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import edu.hardwork.workoutapp.databinding.ActivityExerciseBinding
import edu.hardwork.workoutapp.databinding.DialogCustomBackConfirmationBinding
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding: ActivityExerciseBinding? = null

    //  for rest timer
    private var restTimer: CountDownTimer? = null
    private var restProgress = 0

    //  for exercise timer
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    //  for Text To Speech
    private var tts: TextToSpeech? = null

    //  for Media Player
    private var player: MediaPlayer? = null

    //  for Recycler View
    private var exerciseAdapter: ExerciseStatusAdapter? = null


    private var restTimerDuration: Long = 3
    private var exerciseTimerDuration: Long = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        exerciseList = Constants.defaultExerciseList()

        //for Text To Speech
        tts = TextToSpeech(this, this)

        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        setUpRestView()

        setupExerciseStatusRecyclerView()

    }


    override fun onBackPressed() {
        customDialogForBackButton()
        super.onBackPressed()
    }


    private fun setupExerciseStatusRecyclerView() {
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)

        binding?.rvExerciseStatus?.adapter = exerciseAdapter

    }

    private fun customDialogForBackButton() {
        Log.e("dialog", "working")
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }

        dialogBinding.btnNo.setOnClickListener {

            customDialog.dismiss()
        }

        customDialog.show()

    }

    private fun setUpRestView() {
        try {

            val soundURI =
                Uri.parse("android.resource://edu.hardwork.workoutapp/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, soundURI)
            player?.isLooping = false
            player?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE

        binding?.tvUpComingLabel?.visibility = View.VISIBLE
        binding?.tvUpComingExerciseName?.visibility = View.VISIBLE


        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        binding?.tvUpComingExerciseName?.text =
            exerciseList!![currentExercisePosition + 1].getName()
        setRestProgressBar()
    }

    private fun setUpExerciseView() {
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.tvTitle?.text = "Exercise Name"
        binding?.tvUpComingLabel?.visibility = View.INVISIBLE
        binding?.tvUpComingExerciseName?.visibility = View.INVISIBLE


        if (exerciseTimer != null) {
            restTimer?.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()
        setExerciseProgressBar()
    }

    private fun setRestProgressBar() {
        binding?.progressBar?.progress = restProgress
        restTimer = object : CountDownTimer(restTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setUpExerciseView()
            }

        }.start()
    }


    private fun setExerciseProgressBar() {
        binding?.progressBarEx?.progress = exerciseProgress
        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarEx?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {

                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setUpRestView()
                } else {
                    Toast.makeText(
                        this@ExerciseActivity,
                        "Congratulations! You have Complete the 7 minutes workout.",
                        Toast.LENGTH_LONG,
                    ).show()
                    val intent: Intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        if (exerciseTimer != null) {
            restTimer?.cancel()
            exerciseProgress = 0
        }
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

        if (player != null) {
            player!!.stop()
        }
        binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}