package com.masscode.gonews.data

import com.masscode.gonews.data.source.local.LocalDataSource
import com.masscode.gonews.data.source.remote.RemoteDatasource
import com.masscode.gonews.data.source.remote.network.ApiResponse
import com.masscode.gonews.data.source.remote.response.UserResponse
import com.masscode.gonews.domain.model.User
import com.masscode.gonews.domain.repository.IUserRepository
import com.masscode.gonews.data.utils.AppExecutors
import com.masscode.gonews.data.utils.DataMapper
import com.masscode.gonews.domain.usecase.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val remoteDatasource: RemoteDatasource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
) : IUserRepository {

    override fun getAllArticles(): Flowable<Resource<List<User>>> =
        object : NetworkBoundResource<List<User>, List<UserResponse>>(appExecutors) {
            override fun loadFromDB(): Flowable<List<User>> {
                return localDataSource.getAllArticles().map { DataMapper.mapEntitiesToDomain(it) }
            }

            override fun shouldFetch(data: List<User>?): Boolean =
                data == null || data.isEmpty()
//                true

            override fun createCall(): Flowable<ApiResponse<List<UserResponse>>> =
                remoteDatasource.getAllArticles()

            override fun saveCallResult(data: List<UserResponse>) {
                val articleList = DataMapper.mapResponsesToEntities(data)
                localDataSource.insertArticles(articleList).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            }

        }.asFlowable()
}