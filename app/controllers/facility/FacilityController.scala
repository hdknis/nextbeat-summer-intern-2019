/*
 * This file is part of Nextbeat services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package controllers.facility

import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, MessagesControllerComponents}
import persistence.facility.dao.FacilityDAO

//formForFacility
import persistence.facility.model.Facility.formForFacilitySearch
import persistence.facility.model.Facility.formForFacilityAdd
import persistence.facility.model.Facility.formForFacilityEdit


import persistence.geo.model.Location
import persistence.geo.dao.LocationDAO

//SiteViewValueFacility
import model.site.facility.SiteViewValueFacilityList
import model.site.facility.SiteViewValueFacilityAdd
import model.site.facility.SiteViewValueFacilityEdit
import model.site.facility.SiteViewValueFacilityShow


import model.component.util.ViewValuePageLayout


// 施設
//~~~~~~~~~~~~~~~~~~~~~
class FacilityController @javax.inject.Inject()(
  val facilityDao: FacilityDAO,
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
      facilitySeq <- facilityDao.findAll
    } yield {
      val vv = SiteViewValueFacilityList(
        layout     = ViewValuePageLayout(id = request.uri),
        location   = locSeq,
        facilities = facilitySeq
      )
      Ok(views.html.site.facility.list.Main(vv, formForFacilitySearch))
    }
  }


  /**
    * 施設詳細ページ
    */
  def show(id: Long) = Action.async { implicit request =>
    for {
      locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      facilityOp <- facilityDao.get(id)
    } yield {
      val vv = SiteViewValueFacilityShow(
        layout     = ViewValuePageLayout(id = request.uri),
        location   = locSeq,
        facilitiy   = facilityOp
      )
      Ok(views.html.site.facility.show.Main(vv))
    }
  }

  /**
    * 施設追加ページ
    */
  def add = Action.async { implicit request =>
    for {
      locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
    } yield {
      val vv = SiteViewValueFacilityAdd(
        layout     = ViewValuePageLayout(id = request.uri),
        location   = locSeq
      )
      Ok(views.html.site.facility.add.Main(vv, formForFacilityAdd))
    }
  }


  def create = Action.async { implicit request =>
    formForFacilityAdd.bindFromRequest.fold(
      errors => {
         for {
            locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
         } yield {
            val vv = SiteViewValueFacilityAdd(
              layout     = ViewValuePageLayout(id = request.uri),
              location   = locSeq
            )
            Ok(views.html.site.facility.add.Main(vv, formForFacilityAdd))
         }
        },
       form   => {
         for {
           _ <- facilityDao.add(form)
          } yield {
            Redirect(routes.FacilityController.list)
          }
        }
     )
  }

  /**
   * 施設編集
   */
  def edit(id: Long) = Action.async { implicit request =>
    for {
      locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      facilityOp <- facilityDao.get(id)
    } yield {
      val vv = SiteViewValueFacilityEdit(
        layout     = ViewValuePageLayout(id = request.uri),
        location   = locSeq,
        facilitiy  = facilityOp
      )
      Ok(views.html.site.facility.edit.Main(vv, formForFacilityEdit))
    }
  }


  def update(id: Long) = Action.async { implicit request =>
    formForFacilityEdit.bindFromRequest.fold(
      errors => {
         for {
            locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
            facilityOp  <- facilityDao.get(id)
          } yield {
            val vv = SiteViewValueFacilityShow(
              layout     = ViewValuePageLayout(id = request.uri),
              location   = locSeq,
              facilitiy  = facilityOp
            )
            BadRequest(views.html.site.facility.show.Main(vv))
          }
        },
       form   => {
         facilityDao.update(id,form.name,form.address,form.description)
         for {
          locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
          facilityOp  <- facilityDao.get(id)
          } yield {
            Redirect(routes.FacilityController.show(id))
          }
        }
     )
   }


   /**
   * 施設削除
   */
   def delete(id: Long) = Action.async { implicit request =>
        println("こんにちは１")
      for {
        _ <- facilityDao.delete(id)
      } yield {
        println("こんにちは")
        Redirect(routes.FacilityController.list)
      }
    }


  /**
   * 施設検索
   */
  def search = Action.async { implicit request =>
    formForFacilitySearch.bindFromRequest.fold(
      errors => {
       for {
          locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
          facilitySeq <- facilityDao.findAll
        } yield {
          val vv = SiteViewValueFacilityList(
            layout     = ViewValuePageLayout(id = request.uri),
            location   = locSeq,
            facilities = facilitySeq
          )
          BadRequest(views.html.site.facility.list.Main(vv, errors))
        }
      },
      form   => {
        for {
          locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
          facilitySeq <- form.locationIdOpt match {
            case Some(id) =>
              for {
                locations   <- daoLocation.filterByPrefId(id)
                facilitySeq <- facilityDao.filterByLocationIds(locations.map(_.id))
              } yield facilitySeq
            case None     => facilityDao.findAll
          }
        } yield {
          val vv = SiteViewValueFacilityList(
            layout     = ViewValuePageLayout(id = request.uri),
            location   = locSeq,
            facilities = facilitySeq
          )
          Ok(views.html.site.facility.list.Main(vv, formForFacilitySearch.fill(form)))
        }
      }
    )
  }
}
