import 'package:flutter/material.dart';
import 'other_apis_view.dart';
import '../widgets/bottom_bar.dart';

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
      bottomNavigationBar: const MyBottomBar(),
    );
  }
}
