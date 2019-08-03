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


// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~~~
object Organization {

  // --[ 管理ID ]---------------------------------------------------------------
  type Id = Long

  // --[ フォーム定義 ]---------------------------------------------------------
}

