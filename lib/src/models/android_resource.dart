import 'dart:convert';

/// Information for Android resource.
///
/// The necessary information of a android resource let plugin can load it.
class AndroidResource {
  /// The resource name.
  ///
  /// A fully qualified resource name is of the form "package:type/entry". The
  /// first two components (package and type) are optional if defType
  /// respectively, are specified here.
  /// for example "mipmap/ic_launcher"
  final String name;

  /// Optional default resource type to find
  /// if "type/" is not included in the name. Can be null to require an explicit
  /// type.
  ///
  /// for example the launch icon type is 'mipmap'
  final String? defType;

  const AndroidResource({
    required this.name,
    this.defType,
  });

  Map<String, dynamic> toMap() {
    return {
      'name': name,
      'defType': defType,
    };
  }

  AndroidResource copyWith({
    String? name,
    String? defType,
  }) {
    return AndroidResource(
      name: name ?? this.name,
      defType: defType ?? this.defType,
    );
  }

  factory AndroidResource.fromMap(Map<String, dynamic> map) {
    return AndroidResource(
      name: map['name'] ?? '',
      defType: map['defType'],
    );
  }

  String toJson() => json.encode(toMap());

  factory AndroidResource.fromJson(String source) =>
      AndroidResource.fromMap(json.decode(source));

  @override
  String toString() => 'AndroidResource(name: $name, defType: $defType)';

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is AndroidResource &&
        other.name == name &&
        other.defType == defType;
  }

  @override
  int get hashCode => name.hashCode ^ defType.hashCode;
}
