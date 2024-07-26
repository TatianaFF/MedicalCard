package com.example.data.repository

import com.example.database.dao.AppDao
import com.example.database.models.VisitEntity
import com.example.models.Visit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor (
    private val dao: AppDao
) : AppRepository {
    override suspend fun insertVisit(visit: Visit): Long = dao.insertVisit(visitToVisitEntity(visit))
    override suspend fun updateVisit(visit: Visit) = dao.updateVisit(visitToVisitEntity(visit))

    override suspend fun deleteVisitById(id: Long) = dao.deleteVisitById(id)

    override fun getVisits(): Flow<List<Visit>> = channelFlow {
        dao.getVisits().collectLatest { visitsEntity ->
            send(visitsEntity.map { visitEntityToVisit(it) })
        }
    }

    override fun getVisitById(id: Long): Flow<Visit> = channelFlow {
        dao.getVisitById(id).collectLatest { visitsEntity ->
            send(visitEntityToVisit(visitsEntity))
        }
    }

    private fun visitToVisitEntity(visit: Visit) : VisitEntity = with(visit) {
        VisitEntity(id, name, date, comment, filesPaths)
    }

    private fun visitEntityToVisit(visitEntity: VisitEntity) : Visit = with(visitEntity) {
        Visit(id, name, date, comment, filesPaths)
    }
}