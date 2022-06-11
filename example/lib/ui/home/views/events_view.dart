import 'package:flutter/material.dart';
import 'dart:developer' as dev;
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:simple_bg_location/simple_bg_location.dart';
import 'package:simple_bg_location_example/cubit/position_cubit.dart';

class EventsView extends StatefulWidget {
  const EventsView({Key? key}) : super(key: key);

  @override
  State<EventsView> createState() => _EventsViewState();
}

class _EventsViewState extends State<EventsView> {
  final scrollController = ScrollController();

  void _scrollToBottom() {
    scrollController.animateTo(scrollController.position.maxScrollExtent,
        duration: const Duration(milliseconds: 300), curve: Curves.easeOut);
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(5.0),
      child: BlocBuilder<PositionCubit, PositionState>(
        builder: (context, state) {
          WidgetsBinding.instance
              .addPostFrameCallback((_) => _scrollToBottom());

          return ListView.separated(
            controller: scrollController,
            itemCount: state.positions.length,
            itemBuilder: (context, index) {
              return PositionListTile(position: state.positions[index]);
            },
            separatorBuilder: (_, __) =>
                const Divider(height: 0, thickness: 1.3),
          );
        },
      ),
    );
  }
}

class PositionListTile extends StatelessWidget {
  const PositionListTile({Key? key, required this.position}) : super(key: key);
  final Position position;
  @override
  Widget build(BuildContext context) {
    final title = '[${position.latitude}, ${position.longitude}]';

    final text =
        '{timestamp: ${position.timestamp?.toIso8601String()}, accuracy: ${position.accuracy}}';

    return ListTile(
      title: Text(title),
      subtitle: Text(text),
    );
  }
}
