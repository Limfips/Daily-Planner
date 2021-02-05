package tgd.company.dailyplanner.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.annotations.Nullable
import tgd.company.dailyplanner.R
import tgd.company.dailyplanner.databinding.FragmentLoginBinding
import tgd.company.dailyplanner.other.Status
import tgd.company.dailyplanner.ui.viewmodel.user.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment @Inject constructor(
        @Nullable
        var viewModel: UserViewModel?
) : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = viewModel ?: ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        viewModel!!.init {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToMainFragment()
            )
        }

        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        binding.btnSignIn.setOnClickListener {
            viewModel!!.signIn(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
            )
        }

        binding.btnSignUp.setOnClickListener {
            viewModel!!.signUp(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(),
                    binding.etName.text.toString()
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel!!.loginStatus.observe(viewLifecycleOwner) { result ->
            when(result.status) {
                Status.SUCCESS -> {
                    Snackbar.make(
                            requireActivity().findViewById(R.id.rootLayout),
                            "Successful login",
                            Snackbar.LENGTH_LONG
                    ).show()
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToMainFragment()
                    )
                }
                Status.ERROR -> {
                    Snackbar.make(
                            requireActivity().findViewById(R.id.rootLayout),
                            result.message ?: "An unknown error occurred",
                            Snackbar.LENGTH_LONG
                    ).show()
                }
                Status.LOADING -> {
                    /* NO-OP */
                }
            }
        }
    }
}