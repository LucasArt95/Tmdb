package com.softvision.tmdb.ui.details

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.shape.CutCornerTreatment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.TriangleEdgeTreatment
import com.google.android.material.transition.MaterialContainerTransform
import com.softvision.tmdb.R
import com.softvision.tmdb.base.BaseFragment
import com.softvision.tmdb.databinding.FragmentDetailsBinding
import com.softvision.tmdb.ui.details.model.DetailsAction
import com.softvision.tmdb.ui.details.model.DetailsIntent
import com.softvision.tmdb.ui.details.model.DetailsResult
import com.softvision.tmdb.ui.details.model.DetailsState
import com.softvision.tmdb.utils.themeColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : BaseFragment<DetailsIntent, DetailsAction, DetailsResult, DetailsState>() {

    override val viewModel by viewModels<DetailsViewModel>()
    private lateinit var binding: FragmentDetailsBinding
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            viewModel.submitIntent(DetailsIntent.InitialIntent(args.tmdbItem))
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.animation_duration).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
    }

    override fun initView() {
        binding.fabFavorite.setOnClickListener {
            getCurrentState().itemData?.let {
                viewModel.submitIntent(DetailsIntent.SetFavoriteIntent(it.itemId, it.mediaType, it.isFavorite))
            }
        }

        binding.button.visibility = View.VISIBLE
        val cornerSize = resources.getDimension(R.dimen.corner_size)
        val shapeModel = ShapeAppearanceModel().toBuilder()
            .setAllEdges(TriangleEdgeTreatment(cornerSize, false))
            .setAllCorners(CutCornerTreatment())
            .setAllCornerSizes(cornerSize)
            .build()
        val drawable = MaterialShapeDrawable(shapeModel)
        binding.button.background = drawable
    }

    override fun render(state: DetailsState) {
        binding.flLoading.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        if (state.error != null) showErrorToast(state.error)
        binding.viewData = state
        startPostponedEnterTransition()
    }

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return super.getDefaultViewModelProviderFactory()
    }
}

