package edu.hardwork.workoutapp

object Constants {
    fun defaultExerciseList():ArrayList<ExerciseModel> {
        val exerciseList = ArrayList<ExerciseModel>()
        val jumpingJacks = ExerciseModel(
            1,
            "Jumpping Jacks",
            R.drawable.ic_lunge,
        )
        val pushUp = ExerciseModel(
            1,
            "Push Up",
            R.drawable.ic_push_up,
        )
        val squat = ExerciseModel(
            1,
            "Squat",
            R.drawable.ic_squat,
        )

        exerciseList.add(pushUp)
        exerciseList.add(squat)
        exerciseList.add(jumpingJacks)

        return  exerciseList
    }
}