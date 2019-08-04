/*
 * This file is part of the MARIAGE services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package model.site.organization

import model.component.util.ViewValuePageLayout
import persistence.geo.model.Location
import persistence.organization.model.Organization

// 表示: 施設一覧
//~~~~~~~~~~~~~~~~~~~~~
case class SiteViewValueOrganizationAdd(
  layout:   ViewValuePageLayout,
  location: Seq[Location],
)
