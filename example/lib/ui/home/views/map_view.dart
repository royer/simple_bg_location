import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';
import 'dart:developer' as dev;
import 'package:simple_bg_location_example/cubit/position_cubit.dart';

class MapView extends StatefulWidget {
  const MapView({Key? key}) : super(key: key);

  @override
  State<MapView> createState() => _MapViewState();
}

class _MapViewState extends State<MapView> with AutomaticKeepAliveClientMixin {
  late MapController _mapController;

  final _center = LatLng(49.284260, -123.132448);

  late MapOptions _mapOptions;

  List<LatLng> _trace = [];
  @override
  void initState() {
    super.initState();
    _mapController = MapController();

    _mapOptions = MapOptions(
      center: _center,
      zoom: 13.0,
      onPositionChanged: _onPositionChanged,
    );
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return BlocListener<PositionCubit, PositionState>(
      listener: (context, state) {
        if (state is PositionArrived) {
          final position = state.newPosition;
        }
      },
      child: FlutterMap(
        mapController: _mapController,
        options: _mapOptions,
        children: [
          TileLayerWidget(
            options: TileLayerOptions(
              urlTemplate: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
              subdomains: ['a', 'b', 'c'],
              attributionBuilder: (_) {
                return const Text("© OpenStreetMap contributors");
              },
            ),
          ),
          PolylineLayerWidget(
              options: PolylineLayerOptions(polylines: [
            Polyline(
              points: _trace,
              strokeWidth: 10.0,
              color: const Color.fromRGBO(0, 179, 253, 0.8),
            ),
          ])),
        ],
      ),
    );
  }

  void _onPositionChanged(MapPosition pos, bool hasGesture) {
    _mapOptions.crs.scale(_mapController.zoom);
  }

  @override
  bool get wantKeepAlive => true;
}
