package com.example.brzodolokacije.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Adapters.MyPostsAdapter
import com.example.brzodolokacije.Adapters.ShowPostsHomePageAdapter
import com.example.brzodolokacije.Models.PostPreview
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.Services.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentUserPostsProfileActivity : Fragment() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =inflater.inflate(R.layout.fragment_user_posts_profile_activity, container, false)

        recyclerView=view.findViewById(R.id.rvFragmentUserPostsProfileActivity)

        val bundle = arguments
        val userId = bundle!!.getString("userId")

        val api = RetrofitHelper.getInstance()
        val token = SharedPreferencesHelper.getValue("jwt", requireActivity())
        var data = api.getUsersPosts("Bearer " + token, userId!!);
        data.enqueue(object : Callback<MutableList<PostPreview>> {
            override fun onResponse(
                call: Call<MutableList<PostPreview>>,
                response: Response<MutableList<PostPreview>>
            ) {
                if (response.body() == null) {
                    return
                }
                var posts = response.body()!!.toMutableList<PostPreview>()
                recyclerView.apply {
                    layoutManager= GridLayoutManager(requireContext(),3,
                        GridLayoutManager.VERTICAL,false)
                    adapter= MyPostsAdapter(requireActivity(),posts)
                }
            }

            override fun onFailure(call: Call<MutableList<PostPreview>>, t: Throwable) {
            }
        })


        return view
    }


}