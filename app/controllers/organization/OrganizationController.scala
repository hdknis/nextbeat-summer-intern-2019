/*
 * This file is part of Nextbeat services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package controllers.organization

import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, MessagesControllerComponents}
import persistence.organization.dao.OrganizationDAO

//formFororganization
import persistence.organization.model.Organization.formForOrganizationAdd
import persistence.organization.model.Organization.formForOrganizationEdit


import persistence.geo.model.Location
import persistence.geo.dao.LocationDAO

//SiteViewValueorganization
import model.site.organization.SiteViewValueOrganizationList
import model.site.organization.SiteViewValueOrganizationAdd
import model.site.organization.SiteViewValueOrganizationEdit
import model.site.organization.SiteViewValueOrganizationShow


import model.component.util.ViewValuePageLayout


// 施設
//~~~~~~~~~~~~~~~~~~~~~
class OrganizationController @javax.inject.Inject()(
  val organizationDao: OrganizationDAO,
  val daoLocation: LocationDAO,
  cc: MessagesControllerComponents
) extends AbstractController(cc) with I18nSupport {
  implicit lazy val executionContext = defaultExecutionContext

  /**
    * 施設一覧ページ
    */
  def list = Action.async { implicit request =>
    for {
      locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      organizationSeq <- organizationDao.findAll
    } yield {
      val vv = SiteViewValueOrganizationList(
        layout     = ViewValuePageLayout(id = request.uri),
        location   = locSeq,
        organizations = organizationSeq
      )
      Ok(views.html.site.organization.list.Main(vv))
    }
  }


  /**
    * 施設詳細ページ
    */
  def show(id: Long) = Action.async { implicit request =>
    for {
      locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      organizationOp <- organizationDao.get(id)
    } yield {
      val vv = SiteViewValueOrganizationShow(
        layout     = ViewValuePageLayout(id = request.uri),
        location   = locSeq,
        organization   = organizationOp
      )
      Ok(views.html.site.organization.show.Main(vv))
    }
  }

  /**
    * 施設追加ページ
    */
  def add = Action.async { implicit request =>
    for {
      locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
    } yield {
      val vv = SiteViewValueOrganizationAdd(
        layout     = ViewValuePageLayout(id = request.uri),
        location   = locSeq
      )
      Ok(views.html.site.organization.add.Main(vv, formForOrganizationAdd))
    }
  }


  def create = Action.async { implicit request =>
    formForOrganizationAdd.bindFromRequest.fold(
      errors => {
         for {
            locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
         } yield {
            val vv = SiteViewValueOrganizationAdd(
              layout     = ViewValuePageLayout(id = request.uri),
              location   = locSeq
            )
            Ok(views.html.site.organization.add.Main(vv, formForOrganizationAdd))
         }
        },
       form   => {
         for {
           _ <- organizationDao.add(form)
          } yield {
            Redirect(routes.OrganizationController.list)
          }
        }
     )
  }

  /**
   * 組織編集
   */
  def edit(id: Long) = Action.async { implicit request =>
    for {
      locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      organizationOp <- organizationDao.get(id)
    } yield {
      val vv = SiteViewValueOrganizationEdit(
        layout     = ViewValuePageLayout(id = request.uri),
        location   = locSeq,
        organization  = organizationOp
      )
      Ok(views.html.site.organization.edit.Main(vv, formForOrganizationEdit))
    }
  }


  def update(id: Long) = Action.async { implicit request =>
    formForOrganizationEdit.bindFromRequest.fold(
      errors => {
         for {
            locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
            organizationOp  <- organizationDao.get(id)
          } yield {
            val vv = SiteViewValueOrganizationShow(
              layout     = ViewValuePageLayout(id = request.uri),
              location   = locSeq,
              organization  = organizationOp
            )
            BadRequest(views.html.site.organization.show.Main(vv))
          }
        },
       form   => {
         organizationDao.update(id,form.locationId,form.name_kanji ,form.name_hurigana,form.name_en,form.address)
         for {
          locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
          organizationOp  <- organizationDao.get(id)
          } yield {
            Redirect(routes.OrganizationController.show(id))
          }
        }
     )
   }


   /**
   * 組織削除
   */
   def delete(id: Long) = Action.async { implicit request =>
      for {
        _ <- organizationDao.delete(id)
      } yield {
        Redirect(routes.OrganizationController.list)
      }
    }
}
