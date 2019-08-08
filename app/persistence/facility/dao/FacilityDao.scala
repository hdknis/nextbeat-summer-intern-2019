/*
 * This file is part of the Nextbeat services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package persistence.facility.dao

import java.time.LocalDateTime
import scala.concurrent.Future

import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import persistence.facility.model.Facility
import persistence.geo.model.Location
import persistence.organization.model.Organization

// DAO: 施設情報
//~~~~~~~~~~~~~~~~~~
class FacilityDAO @javax.inject.Inject()(
  val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  // --[ リソース定義 ] --------------------------------------------------------
  lazy val slick = TableQuery[FacilityTable]

  // --[ データ処理定義 ] ------------------------------------------------------
  /**
   * 施設を取得
   */
  def get(id: Facility.Id): Future[Option[Facility]] =
    db.run {
      slick
        .filter(_.id === id)
        .result.headOption
  }


  /**
   * 施設を追加
   */
  def add(data: Facility): Future[Facility.Id] =
    db.run {
      data.id match {
        case None    => slick returning slick.map(_.id) += data
        case Some(_) => DBIO.failed(
          new IllegalArgumentException("The given object is already assigned id.")
        )
      }
  }

 /**
   * 施設を編集
   */
  def update(id: Facility.Id, organizationId: Int, locationId: String,name: String, address: String, description: String) =
    db.run {
      slick
        .filter(_.id === id)
        .map(p => (p.locationId,p.name, p.address, p.description,p.updatedAt))
        .update((locationId, name, address, description,LocalDateTime.now))
  }

  /**
   * 施設を削除する
   */
  def delete(id: Facility.Id) =
    db.run {
      slick
        .filter(_.id === id)
        .delete
  }

  /**
   * 施設を全件取得する
   */
  def findAll: Future[Seq[Facility]] =
    db.run {
      slick.result
  }

  /**
   * 地域から施設を取得
   * 検索業件: ロケーションID
   */
  def filterByLocationIds(locationIds: Seq[Location.Id]): Future[Seq[Facility]] =
    db.run {
      slick
        .filter(_.locationId inSet locationIds)
        .result
  }

  def filterByOrganizationIds(organization_Id: Organization.Id): Future[Seq[Facility]] =
    db.run {
      slick
        .filter(_.organizationId === organization_Id)
        .result
  }


  // --[ テーブル定義 ] --------------------------------------------------------
  class FacilityTable(tag: Tag) extends Table[Facility](tag, "facility") {


    // Table's columns
    /* @1 */ def id                = column[Facility.Id]        ("id", O.PrimaryKey, O.AutoInc)
    /* @2 */ def locationId        = column[Location.Id]        ("location_id")
    /* @3 */ def organizationId    = column[Organization.Id]    ("organization_id")
    /* @4 */ def name              = column[String]             ("name")
    /* @5 */ def address           = column[String]             ("address")
    /* @6 */ def description       = column[String]             ("description")
    /* @7 */ def updatedAt         = column[LocalDateTime]      ("updated_at")
    /* @8 */ def createdAt         = column[LocalDateTime]      ("created_at")

    // The * projection of the table
    def * = (
      id.?, locationId,organizationId, name, address, description,
      updatedAt, createdAt
    ) <> (
      /** The bidirectional mappings : Tuple(table) => Model */
      (Facility.apply _).tupled,
      /** The bidirectional mappings : Model => Tuple(table) */
      (v: TableElementType) => Facility.unapply(v).map(_.copy(
        _7 = LocalDateTime.now
      ))
    )
  }
}
