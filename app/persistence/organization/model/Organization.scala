/*
 * This file is part of the Nextbeat services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package persistence.organization.model

import play.api.data._
import play.api.data.Forms._
import java.time.LocalDateTime
import persistence.geo.model.Location

// 施設情報 (sample)
//~~~~~~~~~~~~~
case class Organization(
  id:             Option[Organization.Id],            // 施設ID
  locationId:     Location.Id,                        // 地域ID
  name_kanji:     String,                             // 組織名(漢字)
  name_hurigana:  String,                             // 組織名(ふりがな)
  name_en:        String,                             // 組織名(英語)
  address:        String,                             // 住所(詳細)
  updatedAt:      LocalDateTime = LocalDateTime.now,  // データ更新日
  createdAt:      LocalDateTime = LocalDateTime.now   // データ作成日
)

// 施設編集
case class OrganizationEdit(
  locationId:     Location.Id,
  name_kanji:     String,
  name_hurigana:  String,
  name_en:        String,
  address:        String,
)


// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~~~
object Organization {

  // --[ 管理ID ]---------------------------------------------------------------
  type Id = Long

   // --[ フォーム定義 ]---------------------------------------------------------
  val formForOrganizationAdd = Form(
    mapping(
      "locationId"      -> nonEmptyText,
      "name_kanji"      -> nonEmptyText,
      "name_hurigana"   -> nonEmptyText,
      "name_en"         -> nonEmptyText,
      "address"         -> nonEmptyText
    )(Function.untupled(
      t => Organization(None, t._1, t._2, t._3, t._4,t._5)
    ))(Organization.unapply(_).map(
      t => (t._2, t._3, t._4, t._5,t._6)
    ))
  )

  val formForOrganizationEdit = Form(
    mapping(
      "locationId"      -> nonEmptyText,
      "name_kanji"      -> nonEmptyText,
      "name_hurigana"   -> nonEmptyText,
      "name_en"         -> nonEmptyText,
      "address"         -> nonEmptyText
    )(OrganizationEdit.apply)(OrganizationEdit.unapply)
  )
}

