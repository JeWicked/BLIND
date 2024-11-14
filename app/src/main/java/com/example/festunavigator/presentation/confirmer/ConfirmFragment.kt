package com.example.festunavigator.presentation.confirmer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.festunavigator.databinding.FragmentConfirmBinding
import com.example.festunavigator.presentation.preview.MainEvent
import com.example.festunavigator.presentation.preview.MainShareModel
import com.example.festunavigator.presentation.preview.MainUiEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.speech.tts.TextToSpeech
import java.util.Locale


class ConfirmFragment : Fragment() {

    private val mainModel: MainShareModel by activityViewModels()

    private var _binding: FragmentConfirmBinding? = null
    private val binding get() = _binding!!

    private val args: ConfirmFragmentArgs by navArgs()
    private val confType by lazy { args.confirmType }

    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mainModel.onEvent(MainEvent.RejectConfObject(confType))
                    findNavController().popBackStack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setEnabled(true)

        binding.acceptButton.setOnClickListener {
            setEnabled(false)
            mainModel.onEvent(MainEvent.AcceptConfObject(confType))
        }

        binding.rejectButton.setOnClickListener {
            setEnabled(false)
            mainModel.onEvent(MainEvent.RejectConfObject(confType))
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainModel.mainUiEvents.collect { uiEvent ->
                    when (uiEvent) {
                        is MainUiEvent.InitSuccess -> {
                            val action = ConfirmFragmentDirections.actionConfirmFragmentToRouterFragment()
                            findNavController().navigate(action)
                        }
                        is MainUiEvent.InitFailed -> {
                            findNavController().popBackStack()
                        }
                        is MainUiEvent.EntryCreated -> {
                            val action = ConfirmFragmentDirections.actionConfirmFragmentToRouterFragment()
                            findNavController().navigate(action)
                        }
                        is MainUiEvent.EntryAlreadyExists -> {
                            val action = ConfirmFragmentDirections.actionConfirmFragmentToRouterFragment()
                            findNavController().navigate(action)
                        }
                        else -> {}
                    }
                }
            }
        }

        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.US)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    val installIntent = Intent()
                    installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                    startActivity(installIntent)
                } else {
                    // TextToSpeech is ready
                    textToSpeech.speak("Was the text placed correctly?", TextToSpeech.QUEUE_FLUSH, null, null)
                }
            } else {
                // Handle initialization error
            }
        }
    }

    private fun setEnabled(enabled: Boolean) {
        binding.acceptButton.isEnabled = enabled
        binding.rejectButton.isEnabled = enabled
    }

    override fun onDestroyView() {
        // Shutdown TextToSpeech when the fragment's view is destroyed
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroyView()
    }

    companion object {
        const val CONFIRM_INITIALIZE = 0
        const val CONFIRM_ENTRY = 1
    }
}