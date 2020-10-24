package ru.skillbranch.skillarticles.viewmodels.auth

import android.text.Spannable
import androidx.core.text.set
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_auth.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.ui.auth.AuthViewModel
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.ui.custom.spans.UnderlineSpan

class AuthFragment : BaseFragment<AuthViewModel>() {

    override val viewModel: AuthViewModel by viewModels()
    override val layout: Int = R.layout.fragment_auth

    override fun setupViews() {
        tv_privacy.setOnClickListener {
            findNavController().navigate(R.id.page_privacy_policy)
        }

        btn_login.setOnClickListener {
//            viewModel.handleLogin(et_login.text.toString(), et_password.text.toString(), null)
            val action  = AuthFragmentDirections.finishLogin()
            findNavController().navigate(action)
        }

        val color = root.attrValue(R.attr.colorPrimary)
        (tv_access_code.text as Spannable).let { it[0..it.length] = UnderlineSpan(color) }
        (tv_privacy.text as Spannable).let { it[0..it.length] = UnderlineSpan(color) }
    }
}