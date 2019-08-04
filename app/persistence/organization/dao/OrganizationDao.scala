/*
 * This file is part of the Nextbeat services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package persistence.organization.dao

import java.time.LocalDateTime
import scala.concurrent.Future

import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import persistence.organization.model.Organization
import persistence.geo.model.Location

// DAO: 施設情報
//~~~~~~~~~~~~~~~~~~
class OrganizationDAO @javax.inject.Inject()(
  val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  // --[ リソース定義 ] --------------------------------------------------------
  lazy val slick = TableQuery[OrganizationTable]

  // --[ データ処理定義 ] ------------------------------------------------------
  /**
   * 施設を取得
   */
  def get(id: Organization.Id): Future[Option[Organization]] =
    db.run {
      slick
        .filter(_.id === id)
        .result.headOption
    }


  /**
   * 施設を追加
   */
  def add(data: Organization): Future[Organization.Id] =
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
  def update(id: Long, locationId: String,name_kanji: String ,name_hurigana: String ,name_en: String ,address: String) =
    db.run {
      slick
        .filter(_.id === id)
        .map(p => (p.locationId,p.name_kanji, p.name_hurigana, p.name_en,p.address,p.updatedAt))
        .update((locationId,name_kanji,name_hurigana,name_en,address,LocalDateTime.now))
  }

  /**
   * 施設を削除する
   */
  def delete(id: Organization.Id) =
    db.run {
      slick
        .filter(_.id === id)
        .delete
  }

  /**
   * 施設を全件取得する
   */
  def findAll: Future[Seq[Organization]] =
    db.run {
      slick.result
  }

  /**
   * 地域から施設を取得
   * 検索業件: ロケーションID
   */
  def filterByLocationIds(locationIds: Seq[Location.Id]): Future[Seq[Organization]] =
    db.run {
      slick
        .filter(_.locationId inSet locationIds)
        .result
    }

  // --[ テーブル定義 ] --------------------------------------------------------
  class OrganizationTable(tag: Tag) extends Table[Organization](tag, "organization") {


    // Table's columns
    /* @1 */ def id              = column[Organization.Id]    ("id", O.PrimaryKey, O.AutoInc)
    /* @2 */ def locationId      = column[Location.Id]    ("location_id")
    /* @3 */ def name_kanji      = column[String]         ("name_kanji")
    /* @4 */ def name_hurigana   = column[String]         ("name_hurigana")
    /* @5 */ def name_en         = column[String]         ("name_en")
    /* @6 */ def address         = column[String]         ("address")
    /* @7 */ def updatedAt       = column[LocalDateTime]  ("updated_at")
    /* @8 */ def createdAt       = column[LocalDateTime]  ("created_at")

    // The * projection of the table
    def * = (
      id.?, locationId, name_kanji,name_hurigana,name_en, address,
      updatedAt, createdAt
    ) <> (
      /** The bidirectional mappings : Tuple(table) => Model */
      (Organization.apply _).tupled,
      /** The bidirectional mappings : Model => Tuple(table) */
      (v: TableElementType) => Organization.unapply(v).map(_.copy(
        _7 = LocalDateTime.now
      ))
    )
  }
}
