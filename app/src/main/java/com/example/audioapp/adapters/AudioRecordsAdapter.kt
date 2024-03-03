import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.audioapp.R
import com.example.audioapp.data.AudioRecords
import java.text.SimpleDateFormat
import java.util.Locale

class AudioRecordsAdapter(private val recordsList: List<AudioRecords>) : RecyclerView.Adapter<AudioRecordsAdapter.AudioViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingPosition: Int? = null

    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text1: TextView = itemView.findViewById(R.id.text1)
        val text2: TextView = itemView.findViewById(R.id.text2)
        val text3: TextView = itemView.findViewById(R.id.text3)
        val imageView: ImageView = itemView.findViewById(R.id.rec)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.audio_record_item, parent, false)
        return AudioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val currentRecord = recordsList[position]
        val recordingIndex = currentRecord.audioPath.indexOf("recording")
        val displayedText = if (recordingIndex != -1) {
            currentRecord.audioPath.substring(recordingIndex)
        } else {
            currentRecord.audioPath
        }
        holder.text1.text = displayedText
        val totalMilliseconds = currentRecord.recordTime
        val totalSeconds = totalMilliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val timeString = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
        holder.text2.text = timeString
        holder.text3.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(currentRecord.recordDate)

        if (currentPlayingPosition == holder.adapterPosition) {
            holder.imageView.setImageResource(R.drawable.playing)
        } else {
            holder.imageView.setImageResource(R.drawable.play)
        }

        holder.imageView.setOnClickListener {
            if (currentPlayingPosition != holder.adapterPosition) {
                currentPlayingPosition = holder.adapterPosition
                playRecord(currentRecord.audioPath)
                notifyDataSetChanged()
            }
        }
    }

    private fun playRecord(audioPath: String) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(audioPath)
        mediaPlayer?.prepare()
        mediaPlayer?.start()

        mediaPlayer?.setOnCompletionListener {
            currentPlayingPosition = null
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return recordsList.size
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mediaPlayer?.release()
    }
}