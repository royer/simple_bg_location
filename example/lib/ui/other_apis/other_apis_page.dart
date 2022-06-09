import 'package:flutter/material.dart';
import 'package:flutter/src/foundation/key.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:simple_bg_location_example/constants.dart';
import 'package:simple_bg_location_example/ui/other_apis/other_apis_view.dart';
import 'package:simple_bg_location_example/ui/widgets/bottom_bar.dart';

class OtherApisPage extends StatelessWidget {
  const OtherApisPage({Key? key}) : super(key: key);

  static String pageNme = "OtherApisPage";
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Other Apis'),
      ),
      body: const OtherApiView(),
      bottomNavigationBar: MyBottomBar(),
    );
  }
}
