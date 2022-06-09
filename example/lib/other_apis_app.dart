import 'package:flutter/material.dart';
import 'package:flutter/src/foundation/key.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:simple_bg_location_example/ui/other_apis/other_apis_page.dart';

class OtherApisApp extends StatelessWidget {
  const OtherApisApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: OtherApisPage(),
    );
  }
}
