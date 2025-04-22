package com.example.honour_qui

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.honour_qui.databinding.TutorialRecyclerBinding
import com.squareup.picasso.Picasso
import androidx.appcompat.app.AppCompatActivity as AppCompatActivity1

class TutorialListAdapter(private var tutorialModelList: MutableList<TutorialModel>) :
    RecyclerView.Adapter<TutorialListAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: TutorialRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: TutorialModel) {
            binding.apply {
                quizTitleText.text = model.title

                // load from drawable resource
                val resID = binding.root.context.resources.getIdentifier(model.image, "drawable", binding.root.context.packageName)
                if (resID != 0) {
                    // load image from picasso
                    Picasso.get().load(resID).into(tutorialImage)
                } else {
                    // error logging
                    Log.e("Image Loading", "Image resource not found: ${model.image}")
                    // placeholder if no image
                    Picasso.get().load(R.drawable.alarm_clock).into(tutorialImage)
                }
                Log.d("TutorialAdapter", "Image: ${model.image}")
                //on click listener
                root.setOnClickListener {
                    val intent = Intent(root.context, TutorialActivity::class.java).apply {
                        putExtra("tutorial_data", model)
                        putExtra("quiz_id", model.quizId)
                    }
                    root.context.startActivity(intent)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            TutorialRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = tutorialModelList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(tutorialModelList[position])
    }

    fun updateList(newList: List<TutorialModel>) {
        tutorialModelList.clear()
        tutorialModelList.addAll(newList)
        notifyDataSetChanged()
    }
}
