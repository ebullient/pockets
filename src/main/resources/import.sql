DROP SEQUENCE IF EXISTS hibernate_sequence;
CREATE SEQUENCE hibernate_sequence START 1;

insert into pocket (id, pocketRef, name, slug, extradimensional, max_weight, max_volume, weight, notes)
values
  (nextval('hibernate_sequence'), 'pouch',           'Coins',     'coins',    false, 6, 1/5, 1, null),
  (nextval('hibernate_sequence'), 'backpack',        'Backpack',  'backpack',  false, 30, 1, 5, null),
  (nextval('hibernate_sequence'), 'handy-haversack', 'Haversack', 'haversack', true, 120, 12, 5, null),
  (nextval('hibernate_sequence'), 'backpack',        'Backpack',  'backpack',  false, 30, 1, 5, null);

insert into item (id, pocket_id, name, slug, quantity, gpValue, weight, tradable)
values (nextval('hibernate_sequence'), 2, 'Rations', 'rations', 10, null, null, true);
