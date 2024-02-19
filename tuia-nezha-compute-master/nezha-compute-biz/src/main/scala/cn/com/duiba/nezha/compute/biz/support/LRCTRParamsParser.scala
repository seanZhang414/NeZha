package cn.com.duiba.nezha.compute.biz.support

import cn.com.duiba.nezha.compute.api.enums.ModelKeyEnum
import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.api.point.Point.ModelBaseInfo
import cn.com.duiba.nezha.compute.biz.constant.{FeatureCollectionConstant, FeatureIdConstant, FeatureMapConstant}


/**
 * Created by pc on 2016/11/22.
 */
object LRCTRParamsParser {

  // TEST
  val FM_CTR_MODEL_TEST_V4_1 = ModelKeyEnum.FM_CTR_MODEL_TEST_V4_1.getIndex
  val FM_CVR_MODEL_TEST_V4_1 = ModelKeyEnum.FM_CVR_MODEL_TEST_V4_1.getIndex

  val FM_CTR_MODEL_TEST_V4 = ModelKeyEnum.FM_CTR_MODEL_TEST_V4.getIndex
  val FM_CVR_MODEL_TEST_V4 = ModelKeyEnum.FM_CVR_MODEL_TEST_V4.getIndex

  //LR_CTR
  val LR_CTR_MODEL_v004 = ModelKeyEnum.LR_CTR_MODEL_v004.getIndex //("mid_lr_ctr_v004", "lr_ctr_v004_20170804"),// 20170804
  val LR_CTR_MODEL_v005 = ModelKeyEnum.LR_CTR_MODEL_v005.getIndex
  val LR_CTR_MODEL_v006 = ModelKeyEnum.LR_CTR_MODEL_v006.getIndex

  //LR_CVR
  val LR_CVR_MODEL_v004= ModelKeyEnum.LR_CVR_MODEL_v004.getIndex //("mid_lr_cvr_v004", "lr_cvr_v004_20170804"),// 20170804
  val LR_CVR_MODEL_v005= ModelKeyEnum.LR_CVR_MODEL_v005.getIndex
  val LR_CVR_MODEL_v006= ModelKeyEnum.LR_CVR_MODEL_v006.getIndex

  //FM_CTR
  val FM_CTR_MODEL_v003= ModelKeyEnum.FM_CTR_MODEL_v003.getIndex
  val FM_CTR_MODEL_v004= ModelKeyEnum.FM_CTR_MODEL_v004.getIndex
  val FM_CTR_MODEL_v006= ModelKeyEnum.FM_CTR_MODEL_v006.getIndex
  val FM_CTR_MODEL_v007= ModelKeyEnum.FM_CTR_MODEL_v007.getIndex

  //FM_CVR
  val FM_CVR_MODEL_v003= ModelKeyEnum.FM_CVR_MODEL_v003.getIndex
  val FM_CVR_MODEL_v004= ModelKeyEnum.FM_CVR_MODEL_v004.getIndex
  val FM_CVR_MODEL_v006= ModelKeyEnum.FM_CVR_MODEL_v006.getIndex
  val FM_CVR_MODEL_v007= ModelKeyEnum.FM_CVR_MODEL_v007.getIndex

  val FM_BE_CVR_MODEL_v001= ModelKeyEnum.FM_BE_CVR_MODEL_v001.getIndex

  def getModelBaseInfo(id: String): ModelBaseInfo = {
    val ret = id match {

      //LR_CTR

      case LR_CTR_MODEL_v004 =>
        ModelBaseInfo(ModelKeyEnum.LR_CTR_MODEL_v004.getIndex,
          FeatureIdConstant.FEATURE_IDX_CTR_V3_20170829,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CTR_V3_20170804,
          FeatureCollectionConstant.FEATURE_Collection_001,5)

      case LR_CTR_MODEL_v005 =>
        ModelBaseInfo(ModelKeyEnum.LR_CTR_MODEL_v005.getIndex,
          FeatureIdConstant.FEATURE_IDX_CTR_V3_20170804,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CTR_V3_20170804,
          FeatureCollectionConstant.FEATURE_Collection_001,5)

      case LR_CTR_MODEL_v006 =>
        ModelBaseInfo(ModelKeyEnum.LR_CTR_MODEL_v006.getIndex,
          FeatureIdConstant.FEATURE_IDX_CTR_V3_20170914,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CTR_V3_20170804,
          FeatureCollectionConstant.FEATURE_Collection_001,5)



      // LR_CVR


      case LR_CVR_MODEL_v004 =>
        ModelBaseInfo(ModelKeyEnum.LR_CVR_MODEL_v004.getIndex,
          FeatureIdConstant.FEATURE_IDX_CVR_V3_20170829,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CVR_V3_20170804,
          FeatureCollectionConstant.FEATURE_Collection_001,5)

      case LR_CVR_MODEL_v005 =>
        ModelBaseInfo(ModelKeyEnum.LR_CVR_MODEL_v005.getIndex,
          FeatureIdConstant.FEATURE_IDX_CVR_V3_20170804,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CVR_V3_20170804,
          FeatureCollectionConstant.FEATURE_Collection_001,5)

      case LR_CVR_MODEL_v006 =>
        ModelBaseInfo(ModelKeyEnum.LR_CVR_MODEL_v006.getIndex,
          FeatureIdConstant.FEATURE_IDX_CVR_V3_20170914,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CVR_V3_20170804,
          FeatureCollectionConstant.FEATURE_Collection_001,5)


      //FM_CTR


      case FM_CTR_MODEL_v003 =>
        ModelBaseInfo(ModelKeyEnum.FM_CTR_MODEL_v003.getIndex,
          FeatureIdConstant.FEATURE_IDX_CTR_V3_20170914,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CTR_V3_20170804,
          FeatureCollectionConstant.FEATURE_Collection_001,5)

      case FM_CTR_MODEL_v004 =>
        ModelBaseInfo(ModelKeyEnum.FM_CTR_MODEL_v004.getIndex,
          FeatureIdConstant.FEATURE_IDX_CTR_V3_20170829,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CTR_V3_20170804,
          FeatureCollectionConstant.FEATURE_Collection_001,5)


      case FM_CTR_MODEL_v006 =>
        ModelBaseInfo(ModelKeyEnum.FM_CTR_MODEL_v006.getIndex,
          FeatureIdConstant.FEATURE_IDX_CTR_V3_20171024,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CTR_V3_20170804,
          FeatureCollectionConstant.FEATURE_Collection_001,5)

      case FM_CTR_MODEL_v007 =>
        ModelBaseInfo(ModelKeyEnum.FM_CTR_MODEL_v007.getIndex,
          FeatureIdConstant.FEATURE_IDX_CTR_V4_20171109,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CTR_V4_20171109,
          FeatureCollectionConstant.FEATURE_Collection_001,5)


      //FM_CVR


      case FM_CVR_MODEL_v003 =>
        ModelBaseInfo(ModelKeyEnum.FM_CVR_MODEL_v003.getIndex,
          FeatureIdConstant.FEATURE_IDX_CVR_V3_20170914,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CVR_V3_20170804,
          FeatureCollectionConstant.FEATURE_Collection_001,5)

      case FM_CVR_MODEL_v004 =>
        ModelBaseInfo(ModelKeyEnum.FM_CVR_MODEL_v004.getIndex,
          FeatureIdConstant.FEATURE_IDX_CVR_V3_20170829,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CVR_V3_20170804,
          FeatureCollectionConstant.FEATURE_Collection_001,5)


      case FM_CVR_MODEL_v006 =>
        ModelBaseInfo(ModelKeyEnum.FM_CVR_MODEL_v006.getIndex,
          FeatureIdConstant.FEATURE_IDX_CVR_V3_20171024,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CVR_V3_20170804,
          FeatureCollectionConstant.FEATURE_Collection_001,5)

      case FM_CVR_MODEL_v007 =>
        ModelBaseInfo(ModelKeyEnum.FM_CVR_MODEL_v007.getIndex,
          FeatureIdConstant.FEATURE_IDX_CVR_V4_20171109,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CVR_V4_20171109,
          FeatureCollectionConstant.FEATURE_Collection_001,5)


        //backend cvr

      case FM_BE_CVR_MODEL_v001 =>
        ModelBaseInfo(ModelKeyEnum.FM_BE_CVR_MODEL_v001.getIndex,
          FeatureIdConstant.FEATURE_IDX_B_CVR_V4_20180427,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_B_CVR_V4_20180427,
          FeatureCollectionConstant.FEATURE_Collection_001,10)


      //DEFAULT




      // Test V4 1
      case FM_CTR_MODEL_TEST_V4_1 =>
        ModelBaseInfo(ModelKeyEnum.FM_CTR_MODEL_TEST_V4_1.getIndex,
          FeatureIdConstant.FEATURE_IDX_CTR_V4_20171109_2,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CTR_V4_20171109,
          FeatureCollectionConstant.FEATURE_Collection_001,5)

      case FM_CVR_MODEL_TEST_V4_1 =>
        ModelBaseInfo(ModelKeyEnum.FM_CVR_MODEL_TEST_V4_1.getIndex,
          FeatureIdConstant.FEATURE_IDX_CVR_V4_20171109_2,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CVR_V4_20171109,
          FeatureCollectionConstant.FEATURE_Collection_001,5)

      // Test_V4
      case FM_CTR_MODEL_TEST_V4 =>
        ModelBaseInfo(ModelKeyEnum.FM_CTR_MODEL_TEST_V4.getIndex,
          FeatureIdConstant.FEATURE_IDX_CTR_V4_20171109,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CTR_V4_20171109,
          FeatureCollectionConstant.FEATURE_Collection_001,5)

      // Test_V4
      case FM_CVR_MODEL_TEST_V4 =>
        ModelBaseInfo(ModelKeyEnum.FM_CVR_MODEL_TEST_V4.getIndex,
          FeatureIdConstant.FEATURE_IDX_CVR_V4_20171109,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CVR_V4_20171109,
          FeatureCollectionConstant.FEATURE_Collection_001,5)


      case _ =>
        ModelBaseInfo(ModelKeyEnum.FM_CTR_MODEL_TEST_V4.getIndex,
          FeatureIdConstant.FEATURE_IDX_CTR_V3_20171025,
          FeatureMapConstant.FEATURE_LDX_LOC_MAP_CTR_V3_20170804,
          FeatureCollectionConstant.FEATURE_Collection_001,5)
    }
    ret
  }




}