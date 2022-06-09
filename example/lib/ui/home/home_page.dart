import 'package:flutter/material.dart';
import 'package:simple_bg_location_example/constants.dart';
import 'package:simple_bg_location_example/ui/other_apis/other_apis_page.dart';
import '../widgets/bottom_bar.dart';
import 'views/views.dart';

class HomePage extends StatelessWidget {
  const HomePage({Key? key}) : super(key: key);

  static String pageNme = "OtherApisPage";

  final List<Tab> tabs = const <Tab>[
    Tab(
      icon: Icon(Icons.map),
    ),
    Tab(
      icon: Icon(Icons.list_alt),
    ),
  ];

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: tabs.length,
      initialIndex: 0,
      child: Scaffold(
        appBar: AppBar(
          title: const Text(appTitle),
          actions: [
            IconButton(
              icon: const Icon(Icons.more_horiz),
              onPressed: () => Navigator.of(context).push(
                  MaterialPageRoute(builder: (_) => const OtherApisPage())),
            ),
          ],
          bottom: TabBar(tabs: tabs),
        ),
        body: const TabBarView(
          physics: NeverScrollableScrollPhysics(),
          children: [
            MapView(),
            EventsView(),
          ],
        ),
        bottomNavigationBar: MyBottomBar(),
      ),
    );
  }
}
