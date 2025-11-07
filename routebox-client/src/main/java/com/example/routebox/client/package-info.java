/**
 * RouteBox client library for the transactional outbox pattern.
 *
 * <p>This package provides:
 *
 * <ul>
 *   <li>{@link com.example.routebox.client.OutboxClient} - Write events to the outbox (concrete
 *       class, no interface needed)
 *   <li>{@link com.example.routebox.client.OutboxFilter} - Deduplicate Kafka consumer messages
 * </ul>
 */
package com.example.routebox.client;
