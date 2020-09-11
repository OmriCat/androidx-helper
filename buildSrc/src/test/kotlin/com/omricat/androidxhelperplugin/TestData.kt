package com.omricat.androidxhelperplugin

object TestData {
    const val androidXUiGroupXml = """<?xml version='1.0' encoding='UTF-8'?>
<androidx.ui>
  <ui-android-text versions="0.1.0-dev01,0.1.0-dev02,0.1.0-dev03,0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08"/>
  <ui-animation versions="0.1.0-dev01,0.1.0-dev02,0.1.0-dev03,0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-animation-core versions="0.1.0-dev01,0.1.0-dev02,0.1.0-dev03,0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-core versions="0.1.0-dev01,0.1.0-dev02,0.1.0-dev03,0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-foundation versions="0.1.0-dev01,0.1.0-dev02,0.1.0-dev03,0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-framework versions="0.1.0-dev01,0.1.0-dev02,0.1.0-dev03,0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10"/>
  <ui-geometry versions="0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-graphics versions="0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-layout versions="0.1.0-dev01,0.1.0-dev02,0.1.0-dev03,0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-livedata versions="0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-material versions="0.1.0-dev01,0.1.0-dev02,0.1.0-dev03,0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-material-icons-core versions="0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-material-icons-extended versions="0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-platform versions="0.1.0-dev01,0.1.0-dev02,0.1.0-dev03,0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10"/>
  <ui-rxjava2 versions="0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-saved-instance-state versions="0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-test versions="0.1.0-dev01,0.1.0-dev02,0.1.0-dev03,0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-text versions="0.1.0-dev01,0.1.0-dev02,0.1.0-dev03,0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-text-android versions="0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-text-core versions="0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-tooling versions="0.1.0-dev01,0.1.0-dev02,0.1.0-dev03,0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-unit versions="0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-util versions="0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
  <ui-vector versions="0.1.0-dev01,0.1.0-dev02,0.1.0-dev03,0.1.0-dev04,0.1.0-dev05,0.1.0-dev06,0.1.0-dev07,0.1.0-dev08,0.1.0-dev09,0.1.0-dev10,0.1.0-dev11"/>
</androidx.ui>
"""

    const val groupsXml =
        """<?xml version="1.0" encoding="UTF-8"?>
                <metadata>
                <com.android.support.constraint/>
                <com.android.databinding/>
                <com.android.support/>
                </metadata>
                """

    const val emptyGroupsListXml = """<?xml version="1.0" encoding="UTF-8"?>
                <metadata>
                </metadata>
                """

}
